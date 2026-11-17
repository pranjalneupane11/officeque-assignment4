package edu.ysu.officeque.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OfficeHourSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long facultyId;
    private boolean active;

    protected OfficeHourSession() { }

    public OfficeHourSession(Long facultyId, boolean active) {
        this.facultyId = facultyId;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getFacultyId() { return facultyId; }
    public boolean isActive() { return active; }
}
