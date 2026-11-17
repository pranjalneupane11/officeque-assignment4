package edu.ysu.officeque.repository;

import java.util.List;
import edu.ysu.officeque.domain.Appointment;
import edu.ysu.officeque.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByFacultyIdAndStatusIn(Long facultyId, List<AppointmentStatus> statuses);
}
