package gov.nysenate.ess.supply.synchronization.service;

import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.synchronization.dao.RequisitionSyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RequisitionSyncAttemptService {
    private final RequisitionService requisitionService;
    private final RequisitionSyncAttemptDao syncAttemptDao;

    public RequisitionSyncAttemptService(RequisitionService requisitionService, RequisitionSyncAttemptDao syncAttemptDao) {
        this.requisitionService = requisitionService;
        this.syncAttemptDao = syncAttemptDao;
    }

    public List<RequisitionSyncAttempt> getSyncAttemptsByReqId(int requisitionId) {
        return syncAttemptDao.findByRequisitionId(requisitionId);
    }
}
