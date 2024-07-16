
TRUNCATE travel.unsubmitted_app;

ALTER TABLE travel.unsubmitted_app
RENAME TO draft;

ALTER TABLE travel.draft
RENAME COLUMN unsubmitted_app_id to draft_id;

ALTER TABLE travel.draft
RENAME COLUMN user_id to user_emp_id;

ALTER TABLE travel.draft
DROP COLUMN traveler_json;

ALTER TABLE travel.draft
DROP COLUMN traveler_dept_head_emp_id;

ALTER TABLE travel.draft
ADD COLUMN traveler_emp_id int NOT NULL;

ALTER TABLE travel.draft
ADD COLUMN updated_date_time timestamp without time zone NOT NULL default now()
