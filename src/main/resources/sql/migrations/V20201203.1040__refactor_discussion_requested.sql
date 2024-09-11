ALTER TABLE travel.app_review_action
DROP COLUMN is_discussion_requested;

ALTER TABLE travel.app_review
ADD COLUMN is_shared boolean NOT NULL DEFAULT false;

UPDATE travel.app_review
SET next_reviewer_role = 'TRAVEL_ADMIN'
WHERE next_reviewer_role = 'DEPUTY_EXECUTIVE_ASSISTANT';

UPDATE travel.app_review_action
SET role = 'TRAVEL_ADMIN'
WHERE role = 'DEPUTY_EXECUTIVE_ASSISTANT';
