package gov.nysenate.ess.supply;

import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.synchronization.dao.SyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemorySyncAttemptDao implements SyncAttemptDao {
    private List<RequisitionSyncAttempt> requisitionSyncAttempts = new ArrayList<>();
    int serialId = 0;

    @Override
    public void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt) {
        requisitionSyncAttempt.setSyncAttemptId(serialId++);
        requisitionSyncAttempts.add(requisitionSyncAttempt);
    }

    @Override
    public List<RequisitionSyncAttempt> findByRequisitionId(int requisitionId) {
        return List.of();
    }

}
