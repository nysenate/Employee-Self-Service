package gov.nysenate.ess.supply.synchronization.model;

import gov.nysenate.ess.supply.requisition.model.SyncStatus;

import java.time.LocalDateTime;
import java.util.List;

public final class RequisitionHistory {
    private int requisition_history_id;
    private int requisition_id;
    private int sync_attempts;
    private LocalDateTime attempt_sync_date;
    private boolean was_successful;
    private String error_info;
    private SyncStatus outcome_sync_status;
    private List<Integer> syncable_line_items;


    public RequisitionHistory() {
    }

    public int getRequisition_history_id() {
        return requisition_history_id;
    }

    public void setRequisition_history_id(int requisition_history_id) {
        this.requisition_history_id = requisition_history_id;
    }

    public int getRequisition_id() {
        return requisition_id;
    }

    public void setRequisition_id(int requisition_id) {
        this.requisition_id = requisition_id;
    }

    public int getSync_attempts() {
        return sync_attempts;
    }

    public void setSync_attempts(int sync_attempts) {
        this.sync_attempts = sync_attempts;
    }

    public LocalDateTime getAttempt_sync_date() {
        return attempt_sync_date;
    }

    public void setAttempt_sync_date(LocalDateTime attempt_sync_date) {
        this.attempt_sync_date = attempt_sync_date;
    }

    public boolean getWas_successful() {
        return was_successful;
    }

    public void setWas_successful(boolean was_successful) {
        this.was_successful = was_successful;
    }

    public String getError_info() {
        return error_info;
    }

    public void setError_info(String error_info) {
        this.error_info = error_info;
    }

    public SyncStatus getOutcome_sync_status() {
        return outcome_sync_status;
    }

    public void setOutcome_sync_status(SyncStatus outcome_sync_status) {
        this.outcome_sync_status = outcome_sync_status;
    }

    public List<Integer> getSyncable_line_items() {
        return syncable_line_items;
    }

    public void setSyncable_line_items(List<Integer> syncable_line_items) {
        this.syncable_line_items = syncable_line_items;
    }
}
