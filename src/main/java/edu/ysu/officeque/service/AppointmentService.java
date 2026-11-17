package edu.ysu.officeque.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import edu.ysu.officeque.domain.Appointment;
import edu.ysu.officeque.domain.AppointmentStatus;
import edu.ysu.officeque.policy.AppointmentConflictPolicy;
import edu.ysu.officeque.policy.AppointmentTimeValidator;
import edu.ysu.officeque.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final Clock clock;
    private final AppointmentTimeValidator times = new AppointmentTimeValidator();
    private final AppointmentConflictPolicy conflicts = new AppointmentConflictPolicy();

    public AppointmentService(AppointmentRepository appointments, Clock clock) {
        this.appointments = appointments;
        this.clock = clock;
    }

    public record AppointmentReceipt(long appointmentId, String status, String message) { }

    @Transactional
    public AppointmentReceipt request(long studentId, long facultyId,
                                      LocalDateTime start, LocalDateTime end) {
        if (facultyId <= 0) {
            throw new IllegalArgumentException("Faculty ID must be positive");
        }
        times.validate(start, end, clock);
        boolean overlaps = appointments.findByFacultyIdAndStatusIn(facultyId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED))
                .stream().anyMatch(existing -> conflicts.overlaps(start, end,
                        existing.getStartTime(), existing.getEndTime()));
        if (overlaps) {
            throw new BusinessConflictException("Requested appointment overlaps an existing booking");
        }
        Appointment saved = appointments.save(new Appointment(studentId, facultyId, start, end));
        return new AppointmentReceipt(saved.getId(), saved.getStatus().name(),
                "Request submitted; awaiting faculty approval");
    }
}
