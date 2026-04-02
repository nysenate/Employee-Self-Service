package gov.nysenate.ess.core.service.pec.external.everfi.sync;

/**
 * What actually happened (or would have happened, in dry run) for a single planned action.
 * {@code message} carries the API/IO error text on {@link SyncOutcome#ERROR}, otherwise empty.
 */
public record SyncResult(
        PlannedAction action,
        SyncOutcome outcome,
        String message
) {
    public static SyncResult success(PlannedAction action) {
        return new SyncResult(action, SyncOutcome.SUCCESS, "");
    }

    public static SyncResult skipped(PlannedAction action) {
        return new SyncResult(action, SyncOutcome.SKIPPED, "");
    }

    public static SyncResult flagged(PlannedAction action) {
        return new SyncResult(action, SyncOutcome.FLAGGED, "");
    }

    public static SyncResult error(PlannedAction action, String message) {
        return new SyncResult(action, SyncOutcome.ERROR, message);
    }
}
