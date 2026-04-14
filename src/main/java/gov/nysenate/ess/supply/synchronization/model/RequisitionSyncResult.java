package gov.nysenate.ess.supply.synchronization.model;

import gov.nysenate.ess.supply.requisition.model.Requisition;

public record RequisitionSyncResult(
        RequisitionSyncAttempt syncAttempt,
        Requisition updatedRequisition
) {
}
