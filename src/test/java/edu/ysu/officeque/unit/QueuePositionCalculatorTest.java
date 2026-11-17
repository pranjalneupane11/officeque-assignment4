package edu.ysu.officeque.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.NoSuchElementException;
import edu.ysu.officeque.domain.QueueStatus;
import edu.ysu.officeque.policy.QueuePositionCalculator;
import edu.ysu.officeque.policy.QueuePositionCalculator.Snapshot;
import org.junit.jupiter.api.Test;

class QueuePositionCalculatorTest {
    private final QueuePositionCalculator calculator = new QueuePositionCalculator();

    @Test
    void firstWaitingStudentHasPositionOneAndZeroWait() {
        var result = calculator.calculate(List.of(new Snapshot(10, QueueStatus.WAITING)), 10, 4);
        assertEquals(1, result.position());
        assertEquals(0, result.estimatedWaitMinutes());
    }

    @Test
    void fourthWaitingStudentHasEstimatedTwelveMinuteWait() {
        var list = List.of(new Snapshot(1, QueueStatus.WAITING), new Snapshot(2, QueueStatus.WAITING),
                new Snapshot(3, QueueStatus.WAITING), new Snapshot(4, QueueStatus.WAITING));
        var result = calculator.calculate(list, 4, 4);
        assertEquals(4, result.position());
        assertEquals(12, result.estimatedWaitMinutes());
    }

    @Test
    void completedOrAbsentEntriesDoNotCountAhead() {
        var list = List.of(new Snapshot(1, QueueStatus.COMPLETED), new Snapshot(2, QueueStatus.WAITING),
                new Snapshot(3, QueueStatus.ABSENT), new Snapshot(4, QueueStatus.WAITING));
        var result = calculator.calculate(list, 4, 4);
        assertEquals(2, result.position());
        assertEquals(4, result.estimatedWaitMinutes());
    }

    @Test
    void unknownEntryIsRejectedInsteadOfGivingFakePosition() {
        assertThrows(NoSuchElementException.class,
                () -> calculator.calculate(List.of(new Snapshot(1, QueueStatus.WAITING)), 99, 4));
    }
}
