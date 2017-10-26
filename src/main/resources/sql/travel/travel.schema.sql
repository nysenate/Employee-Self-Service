--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.9
-- Dumped by pg_dump version 9.5.9

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: travel; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA travel;


ALTER SCHEMA travel OWNER TO postgres;

SET search_path = travel, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: irs_rate; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE irs_rate (
    irs_travel_rate double precision NOT NULL
);


ALTER TABLE irs_rate OWNER TO postgres;

--
-- Name: meal_incidental_rates; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE meal_incidental_rates (
    "totalCost" money NOT NULL,
    "breakfastCost" money NOT NULL,
    "diinnerCost" money NOT NULL,
    "incidentalCost" money NOT NULL
);


ALTER TABLE meal_incidental_rates OWNER TO postgres;

--
-- Name: irs_rate_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY irs_rate
    ADD CONSTRAINT irs_rate_pkey PRIMARY KEY (irs_travel_rate);


--
-- Name: meal_incidental_rates_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY meal_incidental_rates
    ADD CONSTRAINT meal_incidental_rates_pkey PRIMARY KEY ("totalCost");


--
-- PostgreSQL database dump complete
--

