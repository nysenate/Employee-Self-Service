ALTER TABLE travel.app_address RENAME TO address;

ALTER TABLE travel.address
ADD CONSTRAINT address_unique UNIQUE (street_1, street_2, city, county, state, zip_5, zip_4);