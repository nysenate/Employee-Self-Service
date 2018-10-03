ALTER table travel.address
  ADD COLUMN country text;

ALTER TABLE travel.address
DROP CONSTRAINT address_unique;

ALTER TABLE ONLY travel.address
    ADD CONSTRAINT address_unique UNIQUE (street_1, street_2, city, county, state, zip_5, zip_4, country);