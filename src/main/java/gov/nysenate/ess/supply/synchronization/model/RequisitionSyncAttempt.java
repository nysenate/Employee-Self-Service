package gov.nysenate.ess.supply.synchronization.model;

import gov.nysenate.ess.supply.requisition.model.SyncStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class RequisitionSyncAttempt {
    private int syncAttemptId;
    private int requisitionId;
    private int revisionId;
    private int attemptCount;
    private LocalDateTime attemptDateTime;
    private boolean wasSuccessful;
    private String errorMsg;
    private SyncStatus outcomeSyncStatus;
    private List<Integer> syncedItemIds;

    public RequisitionSyncAttempt(int requisitionId, int revisionId, int attemptCount, LocalDateTime datetime) {
        this.requisitionId = requisitionId;
        this.revisionId = revisionId;
        this.attemptCount = attemptCount;
        this.attemptDateTime = datetime;
        this.syncedItemIds = new ArrayList<>();
    }

    public int getSyncAttemptId() {
        return syncAttemptId;
    }

    public void setSyncAttemptId(int syncAttemptId) {
        this.syncAttemptId = syncAttemptId;
    }

    public int getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(int requisitionId) {
        this.requisitionId = requisitionId;
    }

    public void setRevisionId(int revisionId) {
        this.revisionId = revisionId;
    }

    public int getRevisionId() {
        return revisionId;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getAttemptDateTime() {
        return attemptDateTime;
    }

    public void setAttemptDateTime(LocalDateTime attemptDateTime) {
        this.attemptDateTime = attemptDateTime;
    }

    public boolean getWasSuccessful() {
        return wasSuccessful;
    }

    public void setWasSuccessful(boolean wasSuccessful) {
        this.wasSuccessful = wasSuccessful;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public SyncStatus getOutcomeSyncStatus() {
        return outcomeSyncStatus;
    }

    public void setOutcomeSyncStatus(SyncStatus outcomeSyncStatus) {
        this.outcomeSyncStatus = outcomeSyncStatus;
    }

    public List<Integer> getSyncedItemIds() {
        return syncedItemIds;
    }

    public void setSyncedItemIds(List<Integer> syncedItemIds) {
        this.syncedItemIds = syncedItemIds;
    }
}
