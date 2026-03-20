package gov.nysenate.ess.supply.synchronization.SyncStatuses;

import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.model.SkippedReason;
import gov.nysenate.ess.supply.requisition.model.SyncStatus;

public class ModifySyncStatus {

    public ModifySyncStatus() {
    }

    /**
     * Modifies the Requistion depending on the current state of the requisition
     * <p>If everything fails, then the Requisition says in pending</p>
     *
     * @param req
     * @return
     */
    public Requisition modifySyncStatuses(Requisition req, boolean wasSynchronized) {
        if (wasSynchronized) {
            req = successfulRequisition(req);
        } else if (!req.getLineItems().isEmpty() && req.getStatus().equals(RequisitionStatus.APPROVED)) {
            req = erroredRequisition(req);
        } else if (req.getStatus().equals(RequisitionStatus.REJECTED)) {
            req = rejectedRequisition(req);
        } else if (req.getLineItems().isEmpty()) {
            req = noSyncableItems(req);
        }

        return req;
    }

//    boolean hasLineItems = !req.getLineItems().isEmpty();
//    boolean isApproved = req.getStatus().equals(RequisitionStatus.APPROVED);
//    boolean isRejected = req.getStatus().equals(RequisitionStatus.REJECTED);
//
//        if (wasSynchronized) return successfulRequisition(req);
//        if (hasLineItems && isApproved) return erroredRequisition(req);
//        if (!hasLineItems && isRejected) return rejectedRequisition(req);
//        if (isRejected) return rejectedRequisition(req);
//        if (!hasLineItems) return noSyncableItems(req);
//
//        return req;

    public Requisition successfulRequisition(Requisition req) {
        req = req.setSyncStatus(SyncStatus.COMPLETE);
        req = req.setSfmsSyncAttempts(req.getSfmsSyncAttempts() + 1);

        return req;
    }

    public Requisition erroredRequisition(Requisition req) {
        req = req.setSyncStatus(SyncStatus.ERROR);
        req = req.setSfmsSyncAttempts(req.getSfmsSyncAttempts() + 1);
        return req;
    }


    /**
     * <ol>
     * <li>If the req has a rejected status, then it was explicitly skipped</li>
     * </ol>
     *
     * @param req
     * @return
     */
    public Requisition rejectedRequisition(Requisition req) {
        req = req.setSfmsSkippedReason(SkippedReason.REJECTED);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        req = req.setSfmsSyncAttempts(req.getSfmsSyncAttempts() + 1);
        return req;
    }


    /**
     * <ol>
     * <li>If the req has no line items to sync then it should be skipped for not having syncable items</li>
     * </ol>
     *
     * @param req
     * @return
     */
    public Requisition noSyncableItems(Requisition req) {
        req = req.setSfmsSkippedReason(SkippedReason.NO_SYNCABLE_ITEMS);
        req = req.setSyncStatus(SyncStatus.SKIPPED);
        req = req.setSfmsSyncAttempts(req.getSfmsSyncAttempts() + 1);
        return req;
    }

}
