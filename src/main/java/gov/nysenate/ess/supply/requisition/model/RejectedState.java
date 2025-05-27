package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class RejectedState extends RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        // Do nothing
        return requisition;
    }

    @Override
    public Requisition undo(Requisition requisition, LocalDateTime undoDateTime) {
        // Do Nothing
        return requisition;
    }

    @Override
    public Requisition reject(Requisition requisition, LocalDateTime rejectedDateTime) {
        // Do nothing
        return requisition;
    }

    @Override
    public RequisitionStatus getStatus() {
        return RequisitionStatus.REJECTED;
    }
}
