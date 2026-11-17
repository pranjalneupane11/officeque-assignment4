package edu.ysu.officeque.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class QueueEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sessionId;
    private Long studentId;
    private Long facultyId;
    private String reason;
    @Enumerated(EnumType.STRING)
    private QueueStatus status;

    protected QueueEntry() { }

    public QueueEntry(Long sessionId, Long studentId, Long facultyId, String reason) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.facultyId = facultyId;
        this.reason = reason;
        this.status = QueueStatus.WAITING;
    }

    public Long getId() { return id; }
    public Long getSessionId() { return sessionId; }
    public Long getStudentId() { return studentId; }
    public Long getFacultyId() { return facultyId; }
    public String getReason() { return reason; }
    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }
}
