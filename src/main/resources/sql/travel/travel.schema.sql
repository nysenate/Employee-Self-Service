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
    start_date date NOT NULL,
    end_date date NOT NULL,
    irs_travel_rate double precision NOT NULL
);


ALTER TABLE irs_rate OWNER TO postgres;

--
-- Name: meal_incidental_rates; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE meal_incidental_rates (
    "total_cost" integer NOT NULL,
    "breakfast_cost" integer NOT NULL,
    "dinner_cost" integer NOT NULL,
    "incidental_cost" integer NOT NULL
);


ALTER TABLE meal_incidental_rates OWNER TO postgres;

--
-- Name: travel_requestors; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel_requestors (
    emp_id integer NOT NULL,
    requestor_id integer NOT NULL,
    start_date date NOT NULL,
    end_date date
);

ALTER TABLE travel_requestors OWNER TO postgres;

--
-- Name: irs_rate_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY irs_rate
    ADD CONSTRAINT irs_rate_pkey PRIMARY KEY (start_date);


--
-- Name: meal_inc_rates_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY meal_incidental_rates
    ADD CONSTRAINT meal_incidental_rates_pkey PRIMARY KEY ("total_cost");

--
-- Name: travel_requestors_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel_requestors
    ADD CONSTRAINT travel_requestors_pkey PRIMARY KEY (emp_id);

--
-- PostgreSQL database dump complete
--

