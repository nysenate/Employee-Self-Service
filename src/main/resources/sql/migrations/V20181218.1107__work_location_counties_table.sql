CREATE TABLE ess.work_location_county (
  location_code text NOT NULL,
  location_type text NOT NULL,
  county text
);

ALTER TABLE ONLY ess.work_location_county
  ADD CONSTRAINT work_location_county_pkey PRIMARY KEY (location_code, location_type);
