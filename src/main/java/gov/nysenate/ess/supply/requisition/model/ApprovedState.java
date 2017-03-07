package gov.nysenate.ess.supply.requisition.model;

import java.time.LocalDateTime;

public class ApprovedState extends RequisitionState {

    @Override
    public Requisition process(Requisition requisition, LocalDateTime processedDateTime) {
        // Do nothing
        return requisition;
    }

    @Override
    public Requisition reject(Requisition requisition, LocalDateTime rejectedDateTime) {
        // Do nothing, can't reject an Approved Requisition.
        return requisition;
    }

    @Override
    public RequisitionStatus getStatus() {
        return RequisitionStatus.APPROVED;
    }
}
