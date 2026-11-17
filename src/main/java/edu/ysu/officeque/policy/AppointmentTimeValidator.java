package edu.ysu.officeque.policy;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

/** Pure business rule: accept future appointment intervals of up to 60 minutes. */
public class AppointmentTimeValidator {
    public void validate(LocalDateTime start, LocalDateTime end, Clock clock) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end time are required");
        }
        if (!start.isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("Appointment must start in the future");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must follow start time");
        }
        if (Duration.between(start, end).compareTo(Duration.ofMinutes(60)) > 0) {
            throw new IllegalArgumentException("Appointment cannot exceed 60 minutes");
        }
    }
}
