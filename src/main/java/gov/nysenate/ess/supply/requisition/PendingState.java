package gov.nysenate.ess.supply.requisition;

import java.time.LocalDateTime;

public class PendingState implements RequisitionState {

    @Override
    public Requisition process(Requisition requisition) {
        requisition.setProcessedDateTime(LocalDateTime.now());
        requisition.setStatus(RequisitionStatus.PROCESSING);
        requisition.setState(new ProcessingState());
        return requisition;
    }

    @Override
    public Requisition reject(Requisition requisition) {
        return null;
    }
}
