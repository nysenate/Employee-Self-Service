ALTER TABLE IF EXISTS travel.app_approval RENAME TO app_review;
ALTER TABLE IF EXISTS travel.app_approval_action RENAME TO app_review_action;

ALTER TABLE IF EXISTS travel.app_review RENAME app_approval_id TO app_review_id;
ALTER TABLE IF EXISTS travel.app_review RENAME CONSTRAINT app_approval_app_id_fkey TO app_review_app_id_fkey;
ALTER INDEX travel.app_approval_pkey RENAME TO app_review_pkey;

ALTER TABLE IF EXISTS travel.app_review_action RENAME app_approval_id TO app_review_id;
ALTER TABLE IF EXISTS travel.app_review_action RENAME app_approval_action_id TO app_review_action_id;
ALTER TABLE IF EXISTS travel.app_review_action RENAME CONSTRAINT app_approval_action_app_approval_id_fkey TO app_review_action_app_review_id_fkey;
ALTER INDEX travel.app_approval_action_pkey RENAME TO app_review_action_pkey;