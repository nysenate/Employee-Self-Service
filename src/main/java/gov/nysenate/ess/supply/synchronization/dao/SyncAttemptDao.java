package gov.nysenate.ess.supply.synchronization.dao;

import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;

import java.util.List;
import java.util.Optional;

public interface SyncAttemptDao {
    void insertRequisitionSyncAttempt(RequisitionSyncAttempt requisitionSyncAttempt);

}
