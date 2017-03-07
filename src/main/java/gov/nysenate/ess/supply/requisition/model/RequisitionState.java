package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public interface RequisitionState {

    Requisition process(Requisition requisition, LocalDateTime processedDateTime);

    default Requisition reject(Requisition requisition, LocalDateTime rejectedDateTime) {
        requisition = requisition.setRejectedDateTime(rejectedDateTime);
        requisition = requisition.setStatus(RequisitionStatus.REJECTED);
        return requisition.setState(new RejectedState());
    }
}
