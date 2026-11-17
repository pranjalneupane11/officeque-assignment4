package edu.ysu.officeque.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private Long facultyId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    protected Appointment() { }

    public Appointment(Long studentId, Long facultyId, LocalDateTime startTime, LocalDateTime endTime) {
        this.studentId = studentId;
        this.facultyId = facultyId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AppointmentStatus.PENDING;
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public Long getFacultyId() { return facultyId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public AppointmentStatus getStatus() { return status; }
}
