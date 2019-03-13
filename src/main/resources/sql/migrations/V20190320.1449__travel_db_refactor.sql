
DROP TABLE IF EXISTS travel.app CASCADE;
DROP TABLE IF EXISTS travel.app_version CASCADE;
DROP TABLE IF EXISTS travel.address CASCADE;
DROP TABLE IF EXISTS travel.app_lodging_allowance CASCADE;
DROP TABLE IF EXISTS travel.app_meal_allowance CASCADE;
DROP TABLE IF EXISTS travel.app_mileage_allowance CASCADE;
DROP TABLE IF EXISTS travel.app_destination CASCADE;
DROP TABLE IF EXISTS travel.app_leg CASCADE;

CREATE TABLE travel.app (
    app_id SERIAL PRIMARY KEY,
    app_version_id int NOT NULL,
    traveler_id int NOT NULL,
    submitted_date_time timestamp without time zone DEFAULT now() NOT NULL
);

COMMENT ON COLUMN travel.app.app_version_id IS 'The most recent version of the Travel Application.';
COMMENT ON COLUMN travel.app.traveler_id IS 'The employee id of the employee who will be traveling.';
COMMENT ON COLUMN travel.app.submitted_date_time IS 'Date time this travel application was submitted.';

CREATE TABLE travel.app_version (
    app_version_id SERIAL PRIMARY KEY,
    app_id int NOT NULL,
    purpose_of_travel text NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    created_by int NOT NULL
);

COMMENT ON TABLE travel.app_version IS 'An app version represents a travel application at a single point of time. Any change to a travel application should insert a new app version.';
COMMENT ON COLUMN travel.app_version.app_id IS 'The application this version belongs to.';

CREATE TABLE travel.app_leg_destination (
    destination_id SERIAL PRIMARY KEY,
    arrival_date date NOT NULL,
    departure_date date NOT NULL,
    street_1 text,
    street_2 text,
    city text,
    county text,
    state text,
    zip_5 text,
    zip_4 text,
    country text
);

CREATE TABLE travel.app_leg_destination_meal_per_diem (
    destination_id int NOT NULL,
    date date NOT NULL,
    per_diem text NOT NULL
);

CREATE TABLE travel.app_leg_destination_lodging_per_diem (
    destination_id int NOT NULL,
    date date NOT NULL,
    per_diem text NOT NULL
);

CREATE TABLE travel.app_leg (
    leg_id SERIAL PRIMARY KEY,
    from_destination_id int NOT NULL,
    to_destination_id int NOT NULL,
    travel_date date NOT NULL,
    method_of_travel text NOT NULL,
    method_of_travel_description text NOT NULL,
    miles text NOT NULL,
    mileage_rate text NOT NULL,
    is_outbound boolean NOT NULL,
    sequence_no smallint NOT NULL
);

COMMENT ON COLUMN travel.app_leg.sequence_no IS 'Preserves the order of legs.';

CREATE TABLE travel.app_version_leg (
    app_version_leg_id SERIAL PRIMARY KEY,
    app_version_id int NOT NULL,
    leg_id int NOT NULL
);

COMMENT ON TABLE travel.app_version_leg IS 'Join table for app_version and app_leg.';


CREATE TABLE travel.app_allowance (
    allowance_id SERIAL PRIMARY KEY,
    allowance_type text NOT NULL,
    allowance text NOT NULL
);

CREATE TABLE travel.app_version_allowance (
    app_version_allowance_id SERIAL PRIMARY KEY,
    app_version_id int NOT NULL,
    allowance_id int NOT NULL
);

ALTER TABLE travel.app_version_allowance
    ADD CONSTRAINT app_version_allowance_app_version_id_fkey FOREIGN KEY(app_version_id) REFERENCES travel.app_version(app_version_id);

ALTER TABLE travel.app_version_allowance
    ADD CONSTRAINT app_version_allowance_allowance_id_fkey FOREIGN KEY(allowance_id) REFERENCES travel.app_allowance(allowance_id);

ALTER TABLE travel.app_version
    ADD CONSTRAINT app_version_app_id_fkey FOREIGN KEY(app_id) REFERENCES travel.app(app_id);

ALTER TABLE ONLY travel.app_version_leg
    ADD CONSTRAINT app_version_leg_app_version_id_fkey FOREIGN KEY(app_version_id) REFERENCES travel.app_version(app_version_id);

ALTER TABLE ONLY travel.app_version_leg
    ADD CONSTRAINT app_version_leg_leg_id_fkey FOREIGN KEY(leg_id) REFERENCES travel.app_leg(leg_id);

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_from_destination_id_fkey FOREIGN KEY (from_destination_id) REFERENCES travel.app_leg_destination(destination_id);

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_to_destination_id_fkey FOREIGN KEY (to_destination_id) REFERENCES travel.app_leg_destination(destination_id);

ALTER TABLE ONLY travel.app_leg_destination_meal_per_diem
    ADD CONSTRAINT app_leg_destination_meal_per_diem_destination_id_fkey FOREIGN KEY (destination_id) REFERENCES travel.app_leg_destination(destination_id);

ALTER TABLE ONLY travel.app_leg_destination_lodging_per_diem
  ADD CONSTRAINT app_leg_destination_loding_per_diem_destination_id_fkey FOREIGN KEY (destination_id) REFERENCES travel.app_leg_destination(destination_id);


DELETE FROM travel.uncompleted_travel_application;
ALTER TABLE travel.uncompleted_travel_application DROP CONSTRAINT uncompleted_travel_application_pkey;
DROP INDEX travel.uncompleted_travel_application_traveler_id_uindex;
ALTER TABLE travel.uncompleted_travel_application DROP COLUMN id;
ALTER TABLE travel.uncompleted_travel_application DROP COLUMN version_id;
ALTER TABLE travel.uncompleted_travel_application DROP COLUMN submitter_id;
ALTER TABLE travel.uncompleted_travel_application
    ADD CONSTRAINT uncompleted_travel_application_pkey PRIMARY KEY(traveler_id);
