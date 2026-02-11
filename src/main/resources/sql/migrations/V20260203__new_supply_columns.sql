
BEGIN;

-- The new columns for tracking synchronization
-- Check constraint enforces that if the sync reason skipped then we must provide a reason
ALTER TABLE supply.requisition
ADD COLUMN sfms_sync_status TEXT DEFAULT 'PENDING',
ADD COLUMN sfms_sync_attempt_count INT DEFAULT 0,
ADD COLUMN sfms_sync_skip_reason TEXT DEFAULT NULL CHECK (sfms_sync_status = 'SKIPPED' OR sfms_sync_skip_reason IS NULL);


-- Logic for backfilling:

-- Pending: any status that doesn't fit the conditions below and isn't approved yet

-- Complete: approved and sync flag is true

-- Skipped: rejected time is not null

-- Error: approved but saved_in_sfms is false
UPDATE supply.requisition
SET sfms_sync_status = CASE
    WHEN approved_date_time IS NOT NULL AND saved_in_sfms IS TRUE THEN 'COMPLETE'
    WHEN rejected_date_time IS NOT NULL THEN 'SKIPPED'
    WHEN approved_date_time IS NOT NULL AND saved_in_sfms IS FALSE THEN 'ERROR'

    ELSE 'PENDING'
END
WHERE sfms_sync_status = 'PENDING';


-- For all skipped sync status, we'll enter the skipped reason was rejected for now until later.
UPDATE supply.requisition
SET sfms_sync_skip_reason = 'REJECTED'
WHERE requisition.sfms_sync_status = 'SKIPPED';


SELECT * FROM supply.requisition LIMIT 200;

ROLLBACK;
