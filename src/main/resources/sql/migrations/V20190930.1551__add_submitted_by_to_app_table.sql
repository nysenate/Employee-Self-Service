ALTER TABLE travel.app ADD COLUMN submitted_by_id int;

UPDATE travel.app SET submitted_by_id = traveler_id;

ALTER TABLE travel.app ALTER COLUMN submitted_by_id SET NOT NULL;