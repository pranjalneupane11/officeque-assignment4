package edu.ysu.officeque.service;

import java.util.List;
import java.util.NoSuchElementException;
import edu.ysu.officeque.domain.ActorRole;
import edu.ysu.officeque.domain.QueueEntry;
import edu.ysu.officeque.domain.QueueStatus;
import edu.ysu.officeque.policy.QueuePositionCalculator;
import edu.ysu.officeque.policy.QueueTransitionPolicy;
import edu.ysu.officeque.repository.OfficeHourSessionRepository;
import edu.ysu.officeque.repository.QueueEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueueService {
    private final OfficeHourSessionRepository sessions;
    private final QueueEntryRepository entries;
    private final QueuePositionCalculator calculator = new QueuePositionCalculator();
    private final QueueTransitionPolicy transitions = new QueueTransitionPolicy();

    public QueueService(OfficeHourSessionRepository sessions, QueueEntryRepository entries) {
        this.sessions = sessions;
        this.entries = entries;
    }

    public record QueueReceipt(long entryId, String status, int position, int estimatedWaitMinutes) { }

    @Transactional
    public QueueReceipt join(long studentId, long facultyId, long sessionId, String reason) {
        if (facultyId <= 0 || sessionId <= 0 || reason == null || reason.isBlank() || reason.length() > 500) {
            throw new IllegalArgumentException("Valid faculty, session and reason are required");
        }
        var session = sessions.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Office-hour session not found"));
        if (!session.isActive() || !session.getFacultyId().equals(facultyId)) {
            throw new BusinessConflictException("Faculty office hours are not active for this session");
        }
        if (entries.existsBySessionIdAndStudentIdAndStatusIn(sessionId, studentId,
                List.of(QueueStatus.WAITING, QueueStatus.IN_MEETING))) {
            throw new BusinessConflictException("Student already has an active queue entry");
        }
        QueueEntry saved = entries.saveAndFlush(new QueueEntry(sessionId, studentId, facultyId, reason.trim()));
        var snapshots = entries.findBySessionIdOrderByIdAsc(sessionId).stream()
                .map(entry -> new QueuePositionCalculator.Snapshot(entry.getId(), entry.getStatus()))
                .toList();
        var position = calculator.calculate(snapshots, saved.getId(), 4);
        return new QueueReceipt(saved.getId(), saved.getStatus().name(),
                position.position(), position.estimatedWaitMinutes());
    }

    @Transactional
    public void changeStatus(long entryId, QueueStatus target, ActorRole role, long ownerId) {
        QueueEntry entry = entries.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Queue entry not found"));
        long allowedOwner = role == ActorRole.FACULTY ? entry.getFacultyId() : entry.getStudentId();
        if (allowedOwner != ownerId) {
            throw new SecurityException("Cannot modify another user's queue entry");
        }
        entry.setStatus(transitions.change(entry.getStatus(), target, role));
        entries.save(entry);
    }
}
