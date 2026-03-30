CREATE TABLE supply.requisition_sync_attempt
(
    sync_attempt_id     SERIAL PRIMARY KEY,
    requisition_id      INTEGER REFERENCES supply.requisition (requisition_id),
    revision_id         Integer REFERENCES supply.requisition (requisition_id),
    attempt_count       INTEGER,
    attempt_date_time   TIMESTAMP,
    was_successful      BOOLEAN,
    outcome_sync_status TEXT,
    error_msg           TEXT,
    synced_item_ids     INTEGER[],
    UNIQUE (requisition_id, attempt_count)
);