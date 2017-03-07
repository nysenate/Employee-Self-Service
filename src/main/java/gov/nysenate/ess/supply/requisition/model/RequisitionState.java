package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

/**
 * The RequisitionState interface is an implementation of the state pattern
 * such that each implementing class contains the logic for a single RequisitionStatus.
 */
public interface RequisitionState {

    /**
     * Advances a Requisition the its next RequisitionState.
     * Generally follows: PENDING->PROCESSING->COMPLETED->APPROVED
     *
     * @param requisition The requisition to update
     * @param processedDateTime The date time this processing occured.
     * @return An updated requisition.
     */
    Requisition process(Requisition requisition, LocalDateTime processedDateTime);

    /**
     * This method rejects a Requisition.
     * @param requisition The requisition to reject
     * @param rejectedDateTime The date time of the rejection
     * @return An updated Requisition.
     */
    default Requisition reject(Requisition requisition, LocalDateTime rejectedDateTime) {
        requisition = requisition.setRejectedDateTime(rejectedDateTime);
        return requisition.setState(new RejectedState());
    }

    /**
     * Get the RequisitionStatus associated with this state.
     */
    RequisitionStatus getStatus();
}
