package gov.nysenate.ess.supply.requisition.exception;

import java.time.LocalDateTime;

/**
 * This exception is thrown when a requisition update is being saved but the
 * underlying requisition has been modified by another user.
 */
public class ConcurrentRequisitionUpdateException extends RuntimeException {

    private int requisitionId;
    private LocalDateTime lastModified;
    private LocalDateTime persistedLastModified;

    /**
     * @param requisitionId The id of the requisition being updated.
     * @param lastModified The last modified date time according to the requisition that is being saved.
     * @param persistedLastModified The last modified date time according to the requisition in the database.
     */
    public ConcurrentRequisitionUpdateException(int requisitionId, LocalDateTime lastModified, LocalDateTime persistedLastModified) {
        super("Error saving requisition with id: " + requisitionId + ". Update has a last modified date time of " +
             lastModified.toString() + ". But last modified date time is actually " + persistedLastModified.toString() + ".");
        this.requisitionId = requisitionId;
        this.lastModified = lastModified;
        this.persistedLastModified = persistedLastModified;
    }

    public int getRequisitionId() {
        return requisitionId;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public LocalDateTime getPersistedLastModified() {
        return persistedLastModified;
    }
}
