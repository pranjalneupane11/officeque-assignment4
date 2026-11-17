package edu.ysu.officeque.repository;

import java.util.List;
import edu.ysu.officeque.domain.QueueEntry;
import edu.ysu.officeque.domain.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {
    boolean existsBySessionIdAndStudentIdAndStatusIn(Long sessionId, Long studentId, List<QueueStatus> statuses);
    List<QueueEntry> findBySessionIdOrderByIdAsc(Long sessionId);
}
