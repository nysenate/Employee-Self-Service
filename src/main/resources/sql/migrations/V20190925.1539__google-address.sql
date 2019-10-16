-- Extract Address info into a separate table and add GoogleAddress fields.

-- Default values to empty string. We don't want to use null because then the unique constraint wont work.
CREATE TABLE travel.google_address(
    google_address_id SERIAL PRIMARY KEY,
    street_1 text NOT NULL DEFAULT '',
    street_2 text NOT NULL DEFAULT '',
    city text NOT NULL DEFAULT '',
    state text NOT NULL DEFAULT '',
    zip_5 text NOT NULL DEFAULT '',
    zip_4 text NOT NULL DEFAULT '',
    county text NOT NULL DEFAULT '',
    country text NOT NULL DEFAULT '',
    place_id text NOT NULL DEFAULT '',
    name text NOT NULL DEFAULT '',
    formatted_address text NOT NULL DEFAULT ''
);

ALTER TABLE travel.google_address ADD CONSTRAINT google_address_unique UNIQUE(street_1, street_2, city, state, zip_5, zip_4, county, country, place_id);

ALTER TABLE travel.destination ADD COLUMN google_address_id int;

-- Move current address data to google_address table
INSERT INTO travel.google_address(street_1, street_2, city, state, zip_5, zip_4, county, country)
SELECT street_1, street_2, city, state, zip_5, zip_4, county, country
FROM travel.destination
ON CONFLICT DO NOTHING;

-- Set google_address_id's in Destination table
UPDATE travel.destination d
SET google_address_id = ga.google_address_id
FROM travel.google_address ga
WHERE d.street_1 = ga.street_1
AND d.street_2 = ga.street_2
AND d.city = ga.city
AND d.state = ga.state
AND d.zip_5 = ga.zip_5
AND d.zip_4 = ga.zip_4
AND d.county = ga.county
AND d.country = ga.country;

-- Drop address columns from destination table
ALTER TABLE travel.destination DROP COLUMN street_1;
ALTER TABLE travel.destination DROP COLUMN street_2;
ALTER TABLE travel.destination DROP COLUMN city;
ALTER TABLE travel.destination DROP COLUMN state;
ALTER TABLE travel.destination DROP COLUMN zip_5;
ALTER TABLE travel.destination DROP COLUMN zip_4;
ALTER TABLE travel.destination DROP COLUMN county;
ALTER TABLE travel.destination DROP COLUMN country;

-- Set google address id not null
ALTER TABLE travel.destination ALTER COLUMN google_address_id SET NOT NULL;

-- Foreign key
ALTER TABLE travel.destination
    ADD CONSTRAINT destination_google_address_id_fkey FOREIGN KEY(google_address_id) REFERENCES travel.google_address(google_address_id);
