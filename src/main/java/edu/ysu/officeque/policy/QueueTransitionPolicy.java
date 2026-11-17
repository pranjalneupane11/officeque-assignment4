package edu.ysu.officeque.policy;

import edu.ysu.officeque.domain.ActorRole;
import edu.ysu.officeque.domain.QueueStatus;

/** Only legal transitions by the appropriate role are accepted. */
public class QueueTransitionPolicy {
    public QueueStatus change(QueueStatus current, QueueStatus next, ActorRole actor) {
        boolean allowed = switch (actor) {
            case STUDENT -> current == QueueStatus.WAITING && next == QueueStatus.LEFT;
            case FACULTY -> (current == QueueStatus.WAITING
                    && (next == QueueStatus.IN_MEETING || next == QueueStatus.ABSENT))
                    || (current == QueueStatus.IN_MEETING && next == QueueStatus.COMPLETED);
        };
        if (!allowed) {
            throw new IllegalArgumentException("Illegal queue status change");
        }
        return next;
    }
}
