
ALTER TABLE travel.amendment ADD COLUMN event_type text NOT NULL default 'OTHER';
ALTER TABLE travel.amendment ALTER COLUMN event_type drop default;

ALTER TABLE travel.amendment ADD COLUMN event_name text;
ALTER TABLE travel.amendment RENAME COLUMN purpose_of_travel TO additional_purpose;