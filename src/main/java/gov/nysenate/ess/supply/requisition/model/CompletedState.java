package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class CompletedState extends RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        requisition = requisition.setApprovedDateTime(processedDateTime);
        return requisition.setState(new ApprovedState());
    }

    @Override
    public Requisition undo(Requisition requisition, LocalDateTime undoDateTime) {
        requisition = requisition.setCompletedDateTime(null); // Reset completed date time.
        return requisition.setState(new ProcessingState());
    }

    @Override
    public RequisitionStatus getStatus() {
        return RequisitionStatus.COMPLETED;
    }
}
