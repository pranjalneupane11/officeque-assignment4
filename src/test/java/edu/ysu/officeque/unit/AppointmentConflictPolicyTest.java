package edu.ysu.officeque.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import edu.ysu.officeque.policy.AppointmentConflictPolicy;
import org.junit.jupiter.api.Test;

class AppointmentConflictPolicyTest {
    private final AppointmentConflictPolicy policy = new AppointmentConflictPolicy();
    private final LocalDateTime start = LocalDateTime.of(2030, 3, 8, 14, 0);

    @Test
    void partiallyOverlappingIntervalsConflict() {
        assertTrue(policy.overlaps(start.plusMinutes(15), start.plusMinutes(45),
                start, start.plusMinutes(30)));
    }

    @Test
    void fullyContainedIntervalConflicts() {
        assertTrue(policy.overlaps(start.plusMinutes(5), start.plusMinutes(10),
                start, start.plusMinutes(30)));
    }

    @Test
    void touchingEndAndStartDoNotConflict() {
        assertFalse(policy.overlaps(start.plusMinutes(30), start.plusMinutes(60),
                start, start.plusMinutes(30)));
    }

    @Test
    void disjointIntervalsDoNotConflict() {
        assertFalse(policy.overlaps(start.plusMinutes(90), start.plusMinutes(120),
                start, start.plusMinutes(30)));
    }
}
