-- Move the App status from Amendments to the App since there is only 1 status per application.
DROP TABLE travel.amendment_status;

ALTER TABLE travel.app
ADD COLUMN status text;

UPDATE travel.app
SET status = 'NOT_APPLICABLE';

ALTER TABLE travel.app
ALTER COLUMN status SET NOT NULL;

ALTER TABLE travel.app
ADD COLUMN status_note text;
