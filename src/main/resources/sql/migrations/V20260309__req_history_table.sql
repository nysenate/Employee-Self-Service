CREATE TABLE requisitions_history
(
    req_history_id SERIAL INTEGER,
    requisition_id INTEGER,
    sync_status TEXT NOT NULL,
    sync_attempts INTEGER,
    sync_skip_reason TEXT CHECK (( sync_status = "SKIPPED" AND sync_skip_reason != NULL)
                                     OR
                                 (sync_status != "SKIPPED" AND sync_skip_reason = NULL)),
    attempt_sync_date DATE,
    was_synchronized BOOLEAN,
);