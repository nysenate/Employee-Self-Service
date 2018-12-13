package gov.nysenate.ess.supply.socket;

import gov.nysenate.ess.supply.requisition.view.RequisitionView;

/**
 * An event that is handled through the event bus indicating that a requisition has been updated.
 * Contains a {@link RequisitionView} containing the requisition data after the update has been applied.
 */
public class RequisitionUpdateEvent {

    private RequisitionView requisitionView;

    public RequisitionUpdateEvent(RequisitionView requisitionView) {
        this.requisitionView = requisitionView;
    }

    public RequisitionView getRequisitionView() {
        return requisitionView;
    }
}
