package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

/**
 * The RequisitionState interface is an implementation of the state pattern
 * such that each implementing class contains the logic for a single RequisitionStatus.
 */
public abstract class RequisitionState {

    RequisitionState() {}

    public static RequisitionState of(RequisitionStatus status) {
        switch(status) {
            case PENDING:
                return new PendingState();
            case PROCESSING:
                return new ProcessingState();
            case COMPLETED:
                return new CompletedState();
            case APPROVED:
                return new ApprovedState();
            case REJECTED:
                return new RejectedState();
            default:
                return null;
        }
    }

    /**
     * Advances a Requisition the its next RequisitionState.
     * Generally follows: PENDING->PROCESSING->COMPLETED->APPROVED
     *
     * @param requisition The requisition to update
     * @param processedDateTime The date time this processing occured.
     * @return An updated requisition.
     */
    abstract Requisition process(Requisition requisition, LocalDateTime processedDateTime);

    abstract Requisition undo(Requisition requisition, LocalDateTime undoDateTime);

    /**
     * This method rejects a Requisition.
     * @param requisition The requisition to reject
     * @param rejectedDateTime The date time of the rejection
     * @return An updated Requisition.
     */
    protected Requisition reject(Requisition requisition, LocalDateTime rejectedDateTime) {
        requisition = requisition.setRejectedDateTime(rejectedDateTime);
        return requisition.setState(new RejectedState());
    }

    /**
     * Get the RequisitionStatus associated with this state.
     */
    abstract RequisitionStatus getStatus();
}
