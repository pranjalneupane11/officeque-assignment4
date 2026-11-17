package edu.ysu.officeque.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import edu.ysu.officeque.policy.AppointmentTimeValidator;
import org.junit.jupiter.api.Test;

class AppointmentTimeValidatorTest {
    private final AppointmentTimeValidator validator = new AppointmentTimeValidator();
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-16T12:00:00Z"), ZoneOffset.UTC);
    private final LocalDateTime future = LocalDateTime.of(2026, 11, 20, 14, 0);

    @Test
    void validThirtyMinuteSlotAccepted() {
        assertDoesNotThrow(() -> validator.validate(future, future.plusMinutes(30), clock));
    }

    @Test
    void exactlySixtyMinutesAcceptedAsBoundary() {
        assertDoesNotThrow(() -> validator.validate(future, future.plusMinutes(60), clock));
    }

    @Test
    void sixtyMinutesAndOneSecondRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(future, future.plusMinutes(60).plusSeconds(1), clock));
    }

    @Test
    void zeroLengthSlotRejected() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(future, future, clock));
    }

    @Test
    void pastSlotRejected() {
        LocalDateTime yesterday = LocalDateTime.of(2026, 11, 15, 14, 0);
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(yesterday, yesterday.plusMinutes(30), clock));
    }
}
