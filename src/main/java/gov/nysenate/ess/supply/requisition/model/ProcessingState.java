package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class ProcessingState extends RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        requisition = requisition.setCompletedDateTime(processedDateTime);
        return requisition.setState(new CompletedState());
    }

    @Override
    public Requisition undo(Requisition requisition, LocalDateTime undoDateTime) {
        requisition = requisition.setProcessedDateTime(null); // Reset ProcessedDateTime since it is no longer processed.
        return requisition.setState(new PendingState());
    }

    @Override
    public RequisitionStatus getStatus() {
        return RequisitionStatus.PROCESSING;
    }
}
