CREATE TABLE requisitions_history
(
    req_history_id SERIAL INTEGER,
    requisition_id INTEGER REFERENCES requisitions(requisition_id),
    sync_attempts INTEGER UNIQUE(requisition_id, sync_attempts),
    attempt_sync_date TIMESTAMP,
    was_successful BOOLEAN,
    outcome_sync_status TEXT,
    error_info TEXT,
    syncable_line_items INTEGER[]
);