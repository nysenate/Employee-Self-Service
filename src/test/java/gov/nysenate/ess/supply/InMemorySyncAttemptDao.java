package gov.nysenate.ess.supply;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.synchronization.dao.SyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemorySyncAttemptDao implements SyncAttemptDao {
    private List<RequisitionSyncAttempt> requisitionSyncAttempts = new ArrayList<>();
    int serialId = 0;

    @Override
    public void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt) {
        requisitionSyncAttempt.setRequisitionHistoryId(serialId++);
        requisitionSyncAttempts.add(requisitionSyncAttempt);
    }

}
