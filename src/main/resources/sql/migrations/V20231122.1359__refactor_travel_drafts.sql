TRUNCATE TABLE travel.draft;

ALTER TABLE travel.draft
RENAME COLUMN amendment_json TO travel_app_json;
