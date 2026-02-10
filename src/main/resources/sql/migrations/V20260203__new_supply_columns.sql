
-- The new columns for tracking synchronization
-- Check constraint enforces that if the sync reason skipped then we must provide a reason
ALTER TABLE supply.requisition
ADD COLUMN sfms_sync_status VARCHAR(10) DEFAULT 'PENDING',
ADD COLUMN sfms_sync_attempt_count INT DEFAULT 0,
ADD COLUMN sfms_sync_skip_reason  VARCHAR(20) DEFAULT NULL CHECK ( sfms_sync_status == 'SKIPPED' );


-- Logic for backfilling:

-- Pending: any status that doesn't fit the conditions below and isn't approved yet

-- Complete: approved and sync flag is true

-- Skipped: rejected time is not null, default skipped_reason is simply as of now rejected

-- Error: approved but saved_in_sfms is false
UPDATE supply.requisition
SET sfms_sync_status = CASE
    WHEN approved_date_time IS NOT NULL AND saved_in_sfms IS TRUE THEN requisition.sfms_sync_status = 'COMPLETE'
    WHEN rejected_date_time IS NOT NULL THEN requisition.sfms_sync_status = 'SKIPPED' AND requisition.sfms_sync_skip_reason = 'rejected'
    WHEN approved_date_time IS NOT NULL AND saved_in_sfms IS FALSE THEN requisition.sfms_sync_status = 'ERROR'

    ELSE requisition.sfms_sync_status = 'PENDING'
    END;


-- if the requisition was approved but not saved into the database then we'll have to track it as of now
UPDATE supply.requisition
SET sfms_sync_attempt_count = 1
WHERE requisition.saved_in_sfms IS FALSE AND requisition.approved_date_time IS NOT NULL;


