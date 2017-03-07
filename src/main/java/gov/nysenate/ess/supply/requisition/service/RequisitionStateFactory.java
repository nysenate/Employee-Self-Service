package gov.nysenate.ess.supply.requisition.service;

import gov.nysenate.ess.supply.requisition.model.*;

public class RequisitionStateFactory {

    public static RequisitionState stateForStatus(RequisitionStatus status) {
        switch(status) {
            case PENDING:
                return new PendingState();
            case PROCESSING:
                return new ProcessingState();
            case COMPLETED:
                return new CompletedState();
            case APPROVED:
                return new ApprovedState();
            case REJECTED:
                return new RejectedState();
            default:
                return null;
        }
    }
}
