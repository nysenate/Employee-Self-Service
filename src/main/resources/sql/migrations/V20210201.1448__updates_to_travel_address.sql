-- Rename the address table, remove some unused columns.
ALTER TABLE travel.google_address
DROP COLUMN IF EXISTS street_2,
DROP COLUMN IF EXISTS zip_4,
DROP COLUMN IF EXISTS formatted_address;

ALTER TABLE travel.google_address
RENAME TO address;

ALTER SEQUENCE travel.google_address_google_address_id_seq
RENAME TO address_address_id_seq;

ALTER TABLE travel.address
RENAME google_address_id TO address_id;

ALTER TABLE travel.address
RENAME CONSTRAINT google_address_pkey TO address_pkey;

ALTER TABLE travel.address
ADD CONSTRAINT address_unique
UNIQUE (street_1, city, state, zip_5, county, country, place_id);

-- Update references
ALTER TABLE travel.amendment_meal_per_diem
RENAME COLUMN google_address_id TO address_id;

ALTER TABLE travel.amendment_meal_per_diem
RENAME CONSTRAINT amendment_meal_per_diem_google_address_id_fkey
TO amendment_meal_per_diem_address_id_fkey;

ALTER TABLE travel.amendment_lodging_per_diem
RENAME COLUMN google_address_id TO address_id;

ALTER TABLE travel.amendment_lodging_per_diem
RENAME CONSTRAINT amendment_lodging_per_diem_google_address_id_fkey
TO amendment_lodging_per_diem_address_id_fkey;

ALTER TABLE travel.destination
RENAME COLUMN google_address_id TO address_id;

ALTER TABLE travel.destination
RENAME CONSTRAINT destination_google_address_id_fkey
TO destination_address_id_fkey;
