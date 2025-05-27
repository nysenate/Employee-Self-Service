-- Clear out all current data.
TRUNCATE TABLE travel.unsubmitted_app;

-- Add a column for traveler data.
ALTER TABLE travel.unsubmitted_app
ADD COLUMN traveler_json text NOT NULL;

-- All other data is storedin the amendment_json column.
ALTER TABLE travel.unsubmitted_app
RENAME COLUMN app_json TO amendment_json;
