package edu.ysu.officeque.policy;

import java.util.List;
import java.util.NoSuchElementException;
import edu.ysu.officeque.domain.QueueStatus;

/** Queue position is derived from WAITING entries, not stored in the database. */
public class QueuePositionCalculator {
    public record Snapshot(long entryId, QueueStatus status) { }
    public record Position(int position, int estimatedWaitMinutes) { }

    public Position calculate(List<Snapshot> entries, long entryId, int averageVisitMinutes) {
        if (averageVisitMinutes < 0) {
            throw new IllegalArgumentException("Average visit time cannot be negative");
        }
        int waitingAhead = 0;
        for (Snapshot entry : entries) {
            if (entry.entryId() == entryId) {
                if (entry.status() != QueueStatus.WAITING) {
                    throw new IllegalArgumentException("Only waiting entries have a queue position");
                }
                return new Position(waitingAhead + 1, waitingAhead * averageVisitMinutes);
            }
            if (entry.status() == QueueStatus.WAITING) {
                waitingAhead++;
            }
        }
        throw new NoSuchElementException("Queue entry not found");
    }
}
