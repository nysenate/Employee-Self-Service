--
-- PostgreSQL database dump
--

-- Dumped from database version 11.0
-- Dumped by pg_dump version 11.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: travel; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA IF NOT EXISTS travel;


ALTER SCHEMA travel OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: address; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.address (
    id uuid NOT NULL,
    street_1 text,
    street_2 text,
    city text,
    county text,
    state text,
    zip_5 text,
    zip_4 text,
    country text
);


ALTER TABLE travel.address OWNER TO postgres;

--
-- Name: app; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app (
    id uuid NOT NULL,
    current_version_id uuid NOT NULL,
    traveler_id integer NOT NULL,
    submitter_id integer NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE travel.app OWNER TO postgres;

--
-- Name: COLUMN app.traveler_id; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app.traveler_id IS 'The employee id of the employee who will be traveling.';


--
-- Name: COLUMN app.submitter_id; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app.submitter_id IS 'The employee id of whoever submitted the application';


--
-- Name: COLUMN app.created_date_time; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app.created_date_time IS 'Date time this travel application was submitted.';


--
-- Name: app_destination; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app_destination (
    id uuid NOT NULL,
    version_id uuid NOT NULL,
    address_id uuid NOT NULL,
    arrival_date date NOT NULL,
    departure_date date NOT NULL,
    sequence_no smallint NOT NULL
);


ALTER TABLE travel.app_destination OWNER TO postgres;

--
-- Name: COLUMN app_destination.sequence_no; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app_destination.sequence_no IS 'The order of the destinations';


--
-- Name: app_leg; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app_leg (
    id uuid NOT NULL,
    from_address_id uuid NOT NULL,
    to_address_id uuid NOT NULL,
    version_id uuid NOT NULL,
    travel_date date NOT NULL,
    method_of_travel text NOT NULL,
    method_of_travel_description text NOT NULL,
    is_outbound boolean NOT NULL,
    sequence_no smallint NOT NULL
);


ALTER TABLE travel.app_leg OWNER TO postgres;

--
-- Name: COLUMN app_leg.is_outbound; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app_leg.is_outbound IS 'true = outbound leg, false = return leg';


--
-- Name: COLUMN app_leg.sequence_no; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app_leg.sequence_no IS 'The order of this leg';


--
-- Name: app_lodging_allowance; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app_lodging_allowance (
    id uuid NOT NULL,
    version_id uuid NOT NULL,
    address_id uuid NOT NULL,
    date date NOT NULL,
    lodging_rate text NOT NULL,
    is_lodging_requested boolean NOT NULL
);


ALTER TABLE travel.app_lodging_allowance OWNER TO postgres;

--
-- Name: app_meal_allowance; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app_meal_allowance (
    id uuid NOT NULL,
    version_id uuid NOT NULL,
    address_id uuid NOT NULL,
    date date NOT NULL,
    is_meals_requested boolean NOT NULL,
    meal_rate text
);


ALTER TABLE travel.app_meal_allowance OWNER TO postgres;

--
-- Name: app_mileage_allowance; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app_mileage_allowance (
    id uuid NOT NULL,
    version_id uuid NOT NULL,
    leg_id uuid NOT NULL,
    miles text NOT NULL,
    mileage_rate text NOT NULL,
    sequence_no smallint NOT NULL,
    is_outbound boolean
);


ALTER TABLE travel.app_mileage_allowance OWNER TO postgres;

--
-- Name: app_version; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.app_version (
    id uuid NOT NULL,
    app_id uuid NOT NULL,
    purpose_of_travel text NOT NULL,
    tolls_allowance text NOT NULL,
    parking_allowance text NOT NULL,
    alternate_allowance text NOT NULL,
    train_and_plane_allowance text NOT NULL,
    registration_allowance text NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    created_by integer NOT NULL,
    is_deleted boolean NOT NULL
);


ALTER TABLE travel.app_version OWNER TO postgres;

--
-- Name: TABLE app_version; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON TABLE travel.app_version IS 'Represents a single version of a travel application. A new version should be inserted into this table whenever an edit is made so that a full history of edits is maintained.';


--
-- Name: COLUMN app_version.alternate_allowance; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app_version.alternate_allowance IS 'Taxi/bus/subway allowances.';


--
-- Name: COLUMN app_version.created_by; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app_version.created_by IS 'The employee id of whoever made these changes.';


--
-- Name: COLUMN app_version.is_deleted; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.app_version.is_deleted IS 'Logical deletion flag. ';


--
-- Name: irs_mileage_rate; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.irs_mileage_rate (
    start_date date NOT NULL,
    end_date date NOT NULL,
    rate text NOT NULL
);


ALTER TABLE travel.irs_mileage_rate OWNER TO postgres;

--
-- Name: COLUMN irs_mileage_rate.start_date; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.irs_mileage_rate.start_date IS 'The effective start date of this mileage rate, inclusive.';


--
-- Name: COLUMN irs_mileage_rate.end_date; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.irs_mileage_rate.end_date IS 'The effective end date of this mileage rate, inclusinve.';


--
-- Name: COLUMN irs_mileage_rate.rate; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.irs_mileage_rate.rate IS 'The mileage rate whole dollar value - not whole cents. A rate of 54.5 cents should be ''0.545'' in this column';


--
-- Name: travel_requestors; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.travel_requestors (
    emp_id integer NOT NULL,
    requestor_id integer NOT NULL,
    start_date date NOT NULL,
    end_date date
);


ALTER TABLE travel.travel_requestors OWNER TO postgres;

--
-- Name: uncompleted_travel_application; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.uncompleted_travel_application (
    id uuid NOT NULL,
    version_id uuid NOT NULL,
    traveler_id integer NOT NULL,
    submitter_id integer NOT NULL,
    app_json text NOT NULL,
    modified_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE travel.uncompleted_travel_application OWNER TO postgres;

--
-- Name: address address_unique; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.address
    ADD CONSTRAINT address_unique UNIQUE (street_1, street_2, city, county, state, zip_5, zip_4, country);


--
-- Name: address app_address_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.address
    ADD CONSTRAINT app_address_pkey PRIMARY KEY (id);


--
-- Name: app_destination app_destination_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_destination
    ADD CONSTRAINT app_destination_pkey PRIMARY KEY (id);


--
-- Name: app_leg app_leg_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_pkey PRIMARY KEY (id);


--
-- Name: app_lodging_allowance app_lodging_allowance_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_lodging_allowance
    ADD CONSTRAINT app_lodging_allowance_pkey PRIMARY KEY (id);


--
-- Name: app_meal_allowance app_meal_allowance_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_meal_allowance
    ADD CONSTRAINT app_meal_allowance_pkey PRIMARY KEY (id);


--
-- Name: app_mileage_allowance app_mileage_allowance_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_mileage_allowance
    ADD CONSTRAINT app_mileage_allowance_pkey PRIMARY KEY (id);


--
-- Name: app app_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app
    ADD CONSTRAINT app_pkey PRIMARY KEY (id);


--
-- Name: app_version app_version_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_version
    ADD CONSTRAINT app_version_pkey PRIMARY KEY (id);


--
-- Name: irs_mileage_rate irs_mileage_rate_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.irs_mileage_rate
    ADD CONSTRAINT irs_mileage_rate_pkey PRIMARY KEY (start_date, end_date, rate);


--
-- Name: travel_requestors travel_requestors_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.travel_requestors
    ADD CONSTRAINT travel_requestors_pkey PRIMARY KEY (emp_id);


--
-- Name: uncompleted_travel_application uncompleted_travel_application_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.uncompleted_travel_application
    ADD CONSTRAINT uncompleted_travel_application_pkey PRIMARY KEY (id);


--
-- Name: uncompleted_travel_application_traveler_id_uindex; Type: INDEX; Schema: travel; Owner: postgres
--

CREATE UNIQUE INDEX uncompleted_travel_application_traveler_id_uindex ON travel.uncompleted_travel_application USING btree (traveler_id);


--
-- Name: app_destination app_destination_address_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_destination
    ADD CONSTRAINT app_destination_address_id_fkey FOREIGN KEY (address_id) REFERENCES travel.address(id);


--
-- Name: app_destination app_destination_version_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_destination
    ADD CONSTRAINT app_destination_version_id_fkey FOREIGN KEY (version_id) REFERENCES travel.app_version(id);


--
-- Name: app_leg app_leg_from_address_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_from_address_id_fkey FOREIGN KEY (from_address_id) REFERENCES travel.address(id);


--
-- Name: app_leg app_leg_to_address_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_to_address_id_fkey FOREIGN KEY (to_address_id) REFERENCES travel.address(id);


--
-- Name: app_leg app_leg_version_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_version_id_fkey FOREIGN KEY (version_id) REFERENCES travel.app_version(id);


--
-- Name: app_lodging_allowance app_lodging_allowance_address_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_lodging_allowance
    ADD CONSTRAINT app_lodging_allowance_address_id_fkey FOREIGN KEY (address_id) REFERENCES travel.address(id);


--
-- Name: app_lodging_allowance app_lodging_allowance_version_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_lodging_allowance
    ADD CONSTRAINT app_lodging_allowance_version_id_fkey FOREIGN KEY (version_id) REFERENCES travel.app_version(id);


--
-- Name: app_meal_allowance app_meal_allowance_address_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_meal_allowance
    ADD CONSTRAINT app_meal_allowance_address_id_fkey FOREIGN KEY (address_id) REFERENCES travel.address(id);


--
-- Name: app_meal_allowance app_meal_allowance_version_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_meal_allowance
    ADD CONSTRAINT app_meal_allowance_version_id_fkey FOREIGN KEY (version_id) REFERENCES travel.app_version(id);


--
-- Name: app_mileage_allowance app_mileage_allowance_leg_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_mileage_allowance
    ADD CONSTRAINT app_mileage_allowance_leg_id_fkey FOREIGN KEY (leg_id) REFERENCES travel.app_leg(id);


--
-- Name: app_mileage_allowance app_mileage_allowance_version_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_mileage_allowance
    ADD CONSTRAINT app_mileage_allowance_version_id_fkey FOREIGN KEY (version_id) REFERENCES travel.app_version(id);


--
-- Name: app_version app_version_app_id_fkey; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.app_version
    ADD CONSTRAINT app_version_app_id_fkey FOREIGN KEY (app_id) REFERENCES travel.app(id);


--
-- Name: SCHEMA travel; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA travel TO PUBLIC;


--
-- Name: TABLE address; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.address TO PUBLIC;


--
-- Name: TABLE app; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app TO PUBLIC;


--
-- Name: TABLE app_destination; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app_destination TO PUBLIC;


--
-- Name: TABLE app_leg; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app_leg TO PUBLIC;


--
-- Name: TABLE app_lodging_allowance; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app_lodging_allowance TO PUBLIC;


--
-- Name: TABLE app_meal_allowance; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app_meal_allowance TO PUBLIC;


--
-- Name: TABLE app_mileage_allowance; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app_mileage_allowance TO PUBLIC;


--
-- Name: TABLE app_version; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.app_version TO PUBLIC;


--
-- Name: TABLE irs_mileage_rate; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.irs_mileage_rate TO PUBLIC;


--
-- Name: TABLE travel_requestors; Type: ACL; Schema: travel; Owner: postgres
--

GRANT ALL ON TABLE travel.travel_requestors TO PUBLIC;


--
-- PostgreSQL database dump complete
--

