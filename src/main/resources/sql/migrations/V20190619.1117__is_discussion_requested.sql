ALTER TABLE travel.app_review_action
ADD COLUMN is_discussion_requested boolean;

UPDATE travel.app_review_action SET is_discussion_requested = false;

ALTER TABLE travel.app_review_action
ALTER COLUMN is_discussion_requested SET NOT NULL;