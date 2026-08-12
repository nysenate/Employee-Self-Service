ALTER TABLE travel.app_review
    ADD COLUMN policy_type text,
    ADD COLUMN policy_version integer;

-- Preserve the workflow selected for existing reviews. Generic travelers with
-- a pending or completed Department Head step used the standard policy. Generic
-- travelers that never had that step used the Majority-Leader-department-head
-- exception and therefore skipped Department Head review.
UPDATE travel.app_review review
SET policy_type = CASE review.traveler_role
        WHEN 'DEPARTMENT_HEAD' THEN 'SKIP_DEPARTMENT_HEAD'
        WHEN 'MAJORITY_LEADER' THEN 'SKIP_DEPARTMENT_HEAD'
        WHEN 'TRAVEL_ADMIN' THEN 'SECRETARY_ONLY'
        WHEN 'SECRETARY_OF_THE_SENATE' THEN 'TRAVEL_ADMIN_ONLY'
        ELSE CASE
            WHEN review.next_reviewer_role = 'DEPARTMENT_HEAD'
                OR EXISTS (
                    SELECT 1
                    FROM travel.app_review_action action
                    WHERE action.app_review_id = review.app_review_id
                      AND action.role = 'DEPARTMENT_HEAD'
                )
                THEN 'STANDARD'
            ELSE 'SKIP_DEPARTMENT_HEAD'
        END
    END,
    policy_version = 1;

-- The original cursor column was nullable even though the domain always treated
-- NONE as the terminal state. Normalize any legacy nulls before enforcing the
-- aggregate invariant.
UPDATE travel.app_review
SET next_reviewer_role = 'NONE'
WHERE next_reviewer_role IS NULL;

ALTER TABLE travel.app_review
    ALTER COLUMN policy_type SET NOT NULL,
    ALTER COLUMN policy_version SET NOT NULL,
    ADD CONSTRAINT app_review_policy_type_check CHECK (
        policy_type IN (
            'STANDARD',
            'SKIP_DEPARTMENT_HEAD',
            'SECRETARY_ONLY',
            'TRAVEL_ADMIN_ONLY'
        )
    ),
    ADD CONSTRAINT app_review_policy_version_check CHECK (policy_version > 0);

ALTER TABLE travel.app_review
    RENAME COLUMN next_reviewer_role TO pending_reviewer_role;

ALTER TABLE travel.app_review
    ALTER COLUMN pending_reviewer_role SET NOT NULL;

ALTER INDEX travel.app_review_next_reviewer_role_index
    RENAME TO app_review_pending_reviewer_role_index;

ALTER TABLE travel.app_review
    DROP COLUMN traveler_role;

-- Fix a bad record
UPDATE travel.app_review
SET pending_reviewer_role = 'NONE'
WHERE app_review_id = 157
  AND app_id = 187;

UPDATE travel.app
SET status = 'APPROVED'
WHERE app_id = 187
  AND traveler_id = 12415;
