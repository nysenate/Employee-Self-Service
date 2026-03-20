package gov.nysenate.ess.supply.synchronization.model;

import gov.nysenate.ess.supply.requisition.model.SyncStatus;

import java.time.LocalDateTime;
import java.util.List;

public final class RequisitionSyncAttempt {
    private int requisitionHistoryId;
    private int requisitionId;
    private int syncAttempts;
    private LocalDateTime attemptSyncDate;
    private boolean wasSuccessful;
    private String errorInfo;
    private SyncStatus outcomeSyncStatus;
    private List<Integer> syncableLineItems;


    public RequisitionSyncAttempt() {
    }

    public int getRequisitionHistoryId() {
        return requisitionHistoryId;
    }

    public void setRequisitionHistoryId(int requisition_history_id) {
        this.requisitionHistoryId = requisition_history_id;
    }

    public int getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(int requisition_id) {
        this.requisitionId = requisition_id;
    }

    public int getSyncAttempts() {
        return syncAttempts;
    }

    public void setSyncAttempts(int sync_attempts) {
        this.syncAttempts = sync_attempts;
    }

    public LocalDateTime getAttemptSyncDate() {
        return attemptSyncDate;
    }

    public void setAttemptSyncDate(LocalDateTime attempt_sync_date) {
        this.attemptSyncDate = attempt_sync_date;
    }

    public boolean getWasSuccessful() {
        return wasSuccessful;
    }

    public void setWasSuccessful(boolean was_successful) {
        this.wasSuccessful = was_successful;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String error_info) {
        this.errorInfo = error_info;
    }

    public SyncStatus getOutcomeSyncStatus() {
        return outcomeSyncStatus;
    }

    public void setOutcomeSyncStatus(SyncStatus outcome_sync_status) {
        this.outcomeSyncStatus = outcome_sync_status;
    }

    public List<Integer> getSyncableLineItems() {
        return syncableLineItems;
    }

    public void setSyncableLineItems(List<Integer> syncable_line_items) {
        this.syncableLineItems = syncable_line_items;
    }
}
