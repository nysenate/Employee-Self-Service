package gov.nysenate.ess.supply.synchronization.SyncStatuses;

import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.model.SkippedReason;
import gov.nysenate.ess.supply.requisition.model.SyncStatus;
import gov.nysenate.ess.supply.synchronization.service.SfmsSynchronizationService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModifySyncStatus {
    private final SfmsSynchronizationService sfmsService;

    public ModifySyncStatus(SfmsSynchronizationService sfmsService) {
        this.sfmsService = sfmsService;
    }

    /**
     * Modifies the Requistion depending on the current state of the requisition
     *  <p>If everything fails, then the Requisition says in pending</p>
     * @param req
     * @return
     */
    public Requisition modifySyncStatuses(Requisition req){
        if (req.getStatus().equals(RequisitionStatus.REJECTED)) {
            req = rejectedRequisition(req);
        } else if (req.getLineItems().isEmpty() && req.getStatus().equals(RequisitionStatus.REJECTED)) { // edge case you explained
            req = rejectedAndNoLineItems(req);
        } else if (sfmsService.getLineItemsRequiringSync(req.getLineItems()).isEmpty()) {
            req = noSyncableItems(req);
        } else {
            req = req.setSyncStatus(SyncStatus.PENDING);
        }

        return req;
    }


    /**
     * <ol>
     * <li>If the req has a rejected status, then it was explicitly skipped</li>
     * </ol>
     * @param req
     * @return
     */
    public Requisition rejectedRequisition(Requisition req){
        req = req.setSfmsSkippedReason(SkippedReason.REJECTED);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        req = req.setSfmsSyncAttempts(req.getSfmsSyncAttempts() + 1);
        return req;
    }

    /**
     * <ol>
     * <li>If the req has no line items to sync and was rejected then we will say its skipped and rejected</li>
     * </ol>
     * @param req
     * @return
     */
    public Requisition rejectedAndNoLineItems (Requisition req){
        req = req.setSfmsSkippedReason(SkippedReason.REJECTED);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        req = req.setSfmsSyncAttempts(req.getSfmsSyncAttempts() + 1);
        return req;
    }

    /**
     * <ol>
     * <li>If the req has no line items to sync then it should be skipped for not having syncable items</li>
     * </ol>
     * @param req
     * @return
     */
    public Requisition noSyncableItems(Requisition req){
        req = req.setSfmsSkippedReason(SkippedReason.NO_SYNCABLE_ITEMS);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        return req;
    }

}
