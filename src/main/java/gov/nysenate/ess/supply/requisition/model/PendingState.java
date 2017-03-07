package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class PendingState extends RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        requisition = requisition.setProcessedDateTime(processedDateTime);
        return requisition.setState(new ProcessingState());
    }

    @Override
    public RequisitionStatus getStatus() {
        return RequisitionStatus.PENDING;
    }
}
