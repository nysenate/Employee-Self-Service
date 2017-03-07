package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class PendingState implements RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        requisition = requisition.setProcessedDateTime(processedDateTime);
        requisition = requisition.setStatus(RequisitionStatus.PROCESSING);
        return requisition.setState(new ProcessingState());
    }
}
