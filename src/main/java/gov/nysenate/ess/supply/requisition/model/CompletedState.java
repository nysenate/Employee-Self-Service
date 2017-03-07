package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class CompletedState implements RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        requisition = requisition.setApprovedDateTime(processedDateTime);
        requisition = requisition.setStatus(RequisitionStatus.APPROVED);
        return requisition.setState(new ApprovedState());
    }
}
