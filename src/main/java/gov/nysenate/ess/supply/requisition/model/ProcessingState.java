package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class ProcessingState implements RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        requisition = requisition.setCompletedDateTime(processedDateTime);
        requisition = requisition.setStatus(RequisitionStatus.COMPLETED);
        return requisition.setState(new CompletedState());
    }
}
