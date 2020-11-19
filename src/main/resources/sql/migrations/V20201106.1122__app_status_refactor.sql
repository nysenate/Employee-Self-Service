-- Move the App status from Amendments to the App since there is only 1 status per application.
DROP TABLE travel.amendment_status;

ALTER TABLE travel.app
ADD COLUMN status text;

ALTER TABLE travel.app
ADD COLUMN status_note text;
