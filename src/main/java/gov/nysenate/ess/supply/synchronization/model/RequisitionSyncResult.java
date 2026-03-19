package gov.nysenate.ess.supply.synchronization.model;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.view.SfmsRequisitionView;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import org.springframework.dao.DataAccessException;

public class RequisitionSyncResult {
    private RequisitionSyncAttempt syncAttempt;
    private Requisition updatedRequisition;

    public RequisitionSyncResult(RequisitionSyncAttempt syncAttempt, Requisition updatedRequisition) {
        this.syncAttempt = syncAttempt;
        this.updatedRequisition = updatedRequisition;
    }

    public RequisitionSyncAttempt getSyncAttempt() {
        return syncAttempt;
    }

    public void setSyncAttempt(RequisitionSyncAttempt syncAttempt) {
        this.syncAttempt = syncAttempt;
    }

    public Requisition getUpdatedRequisition() {
        return updatedRequisition;
    }

    public void setUpdatedRequisition(Requisition updatedRequisition) {
        this.updatedRequisition = updatedRequisition;
    }
}
