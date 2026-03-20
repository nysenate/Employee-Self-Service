CREATE TABLE supply.requisition_sync_attempt
(
    req_history_id      SERIAL PRIMARY KEY,
    requisition_id      INTEGER REFERENCES supply.requisition (requisition_id),
    sync_attempts       INTEGER,
    attempt_sync_date   TIMESTAMP,
    was_successful      BOOLEAN,
    outcome_sync_status TEXT,
    error_info          TEXT,
    syncable_line_items INTEGER[],
    UNIQUE (requisition_id, sync_attempts)
);