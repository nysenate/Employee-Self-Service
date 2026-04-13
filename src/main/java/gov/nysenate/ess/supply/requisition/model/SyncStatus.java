package gov.nysenate.ess.supply.requisition.model;

public enum SyncStatus {
    /**
     * Sync completed successfully.
     */
    COMPLETE,
    /**
     * Sync was not needed, such as for rejected requests or requests without SFMS-tracked items.
     */
    SKIPPED,
    /**
     * Sync failed.
     */
    ERROR,
    /**
     * Waiting to be synchronized with SFMS.
     */
    PENDING
}
