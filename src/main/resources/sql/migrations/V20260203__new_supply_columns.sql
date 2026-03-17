-- The new columns for tracking synchronization
-- Check constraint enforces that if the sync reason skipped then we must provide a reason
ALTER TABLE supply.requisition
    ADD COLUMN sfms_sync_status        TEXT DEFAULT 'PENDING',
    ADD COLUMN sfms_sync_attempt_count INT  DEFAULT 0,
    ADD COLUMN sfms_sync_skip_reason   TEXT DEFAULT NULL;


-- Logic for backfilling:

-- Pending: any status that doesn't fit the conditions below and isn't approved yet

-- Complete: approved and sync flag is true

-- Skipped: rejected time is not null

-- Error: approved but saved_in_sfms is false
UPDATE supply.requisition
SET sfms_sync_status = CASE
                           WHEN requisition.last_sfms_sync_date_time IS NOT NULL AND requisition.saved_in_sfms IS FALSE
                               THEN 'ERROR'
                           WHEN approved_date_time IS NOT NULL AND saved_in_sfms IS TRUE THEN 'COMPLETE'
                           WHEN approved_date_time IS NOT NULL AND saved_in_sfms IS FALSE THEN 'PENDING'
                           WHEN rejected_date_time IS NOT NULL THEN 'SKIPPED'
                           ELSE 'PENDING'
    END
WHERE requisition.sfms_sync_status = 'PENDING';

-- For all skipped sync status, we'll enter the skipped reason was rejected for now until later.
UPDATE supply.requisition
SET sfms_sync_skip_reason = 'REJECTED'
WHERE requisition.sfms_sync_status = 'SKIPPED';


--Add alter check after filling the skipped rows since adding it at the start triggers the check constraint
ALTER TABLE supply.requisition
    ADD CONSTRAINT skipped_with_skip_reason CHECK (
        (sfms_sync_status != 'SKIPPED' AND sfms_sync_skip_reason IS NULL)
            OR
        (sfms_sync_status = 'SKIPPED' AND sfms_sync_skip_reason IS NOT NULL));




