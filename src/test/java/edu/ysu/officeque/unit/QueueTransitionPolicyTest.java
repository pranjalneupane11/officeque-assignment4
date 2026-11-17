package edu.ysu.officeque.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.ysu.officeque.domain.ActorRole;
import edu.ysu.officeque.domain.QueueStatus;
import edu.ysu.officeque.policy.QueueTransitionPolicy;
import org.junit.jupiter.api.Test;

class QueueTransitionPolicyTest {
    private final QueueTransitionPolicy policy = new QueueTransitionPolicy();

    @Test
    void facultyMayStartWaitingVisit() {
        assertEquals(QueueStatus.IN_MEETING,
                policy.change(QueueStatus.WAITING, QueueStatus.IN_MEETING, ActorRole.FACULTY));
    }

    @Test
    void facultyMayCompleteStartedVisit() {
        assertEquals(QueueStatus.COMPLETED,
                policy.change(QueueStatus.IN_MEETING, QueueStatus.COMPLETED, ActorRole.FACULTY));
    }

    @Test
    void studentMayLeaveWhileWaiting() {
        assertEquals(QueueStatus.LEFT,
                policy.change(QueueStatus.WAITING, QueueStatus.LEFT, ActorRole.STUDENT));
    }

    @Test
    void studentCannotCompleteAnotherVisit() {
        assertThrows(IllegalArgumentException.class,
                () -> policy.change(QueueStatus.IN_MEETING, QueueStatus.COMPLETED, ActorRole.STUDENT));
    }

    @Test
    void facultyCannotCompleteVisitThatNeverStarted() {
        assertThrows(IllegalArgumentException.class,
                () -> policy.change(QueueStatus.WAITING, QueueStatus.COMPLETED, ActorRole.FACULTY));
    }
}
