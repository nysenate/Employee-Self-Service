package gov.nysenate.ess.supply.synchronization.model;

import gov.nysenate.ess.supply.requisition.model.SyncStatus;

import java.time.LocalDateTime;
import java.util.List;

public final class RequisitionSyncAttempt {
    private int requisition_history_id;
    private int requisition_id;
    private int sync_attempts;
    private LocalDateTime attempt_sync_date;
    private boolean was_successful;
    private String error_info;
    private SyncStatus outcome_sync_status;
    private List<Integer> syncable_line_items;


    public RequisitionSyncAttempt() {
    }

    public int getRequisitionHistoryId() {
        return requisition_history_id;
    }

    public void setRequisitionHistoryId(int requisition_history_id) {
        this.requisition_history_id = requisition_history_id;
    }

    public int getRequisitionId() {
        return requisition_id;
    }

    public void setRequisitionId(int requisition_id) {
        this.requisition_id = requisition_id;
    }

    public int getSyncAttempts() {
        return sync_attempts;
    }

    public void setSyncAttempts(int sync_attempts) {
        this.sync_attempts = sync_attempts;
    }

    public LocalDateTime getAttemptSyncDate() {
        return attempt_sync_date;
    }

    public void setAttemptSyncDate(LocalDateTime attempt_sync_date) {
        this.attempt_sync_date = attempt_sync_date;
    }

    public boolean getWasSuccessful() {
        return was_successful;
    }

    public void setWasSuccessful(boolean was_successful) {
        this.was_successful = was_successful;
    }

    public String getErrorInfo() {
        return error_info;
    }

    public void setErrorInfo(String error_info) {
        this.error_info = error_info;
    }

    public SyncStatus getOutcomeSyncStatus() {
        return outcome_sync_status;
    }

    public void setOutcomeSyncStatus(SyncStatus outcome_sync_status) {
        this.outcome_sync_status = outcome_sync_status;
    }

    public List<Integer> getSyncableLineItems() {
        return syncable_line_items;
    }

    public void setSyncableLineItems(List<Integer> syncable_line_items) {
        this.syncable_line_items = syncable_line_items;
    }
}
