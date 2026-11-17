package edu.ysu.officeque.policy;

import java.time.LocalDateTime;

/** Uses half-open intervals [start, end); touching endpoints do not conflict. */
public class AppointmentConflictPolicy {
    public boolean overlaps(LocalDateTime requestedStart, LocalDateTime requestedEnd,
                            LocalDateTime existingStart, LocalDateTime existingEnd) {
        return requestedStart.isBefore(existingEnd) && existingStart.isBefore(requestedEnd);
    }
}
