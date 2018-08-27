--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 10.3

-- Started on 2018-03-21 10:39:14 EDT

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

CREATE SCHEMA travel;


ALTER SCHEMA travel OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

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
-- Dependencies: 202
-- Name: COLUMN irs_mileage_rate.start_date; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.irs_mileage_rate.start_date IS 'The effective start date of this mileage rate, inclusive.';


--
-- Dependencies: 202
-- Name: COLUMN irs_mileage_rate.end_date; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.irs_mileage_rate.end_date IS 'The effective end date of this mileage rate, inclusive.';


--
-- Dependencies: 202
-- Name: COLUMN irs_mileage_rate.rate; Type: COMMENT; Schema: travel; Owner: postgres
--

COMMENT ON COLUMN travel.irs_mileage_rate.rate IS 'The mileage rate whole dollar value - not whole cents. A rate of 54.5 cents should be ''0.545'' in this column';


--
-- Name: meal_rate; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.meal_rate (
    id integer NOT NULL,
    start_date date NOT NULL,
    end_date date
);


ALTER TABLE travel.meal_rate OWNER TO postgres;

--
-- Name: meal_rate_id_seq; Type: SEQUENCE; Schema: travel; Owner: postgres
--

CREATE SEQUENCE travel.meal_rate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE travel.meal_rate_id_seq OWNER TO postgres;

--
-- Dependencies: 200
-- Name: meal_rate_id_seq; Type: SEQUENCE OWNED BY; Schema: travel; Owner: postgres
--

ALTER SEQUENCE travel.meal_rate_id_seq OWNED BY travel.meal_rate.id;


--
-- Name: meal_tier; Type: TABLE; Schema: travel; Owner: postgres
--

CREATE TABLE travel.meal_tier (
    id integer NOT NULL,
    tier text NOT NULL,
    total text NOT NULL,
    incidental text NOT NULL
);


ALTER TABLE travel.meal_tier OWNER TO postgres;

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
-- Name: meal_rate id; Type: DEFAULT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.meal_rate ALTER COLUMN id SET DEFAULT nextval('travel.meal_rate_id_seq'::regclass);


--
-- Name: irs_mileage_rate irs_mileage_rate_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.irs_mileage_rate
    ADD CONSTRAINT irs_mileage_rate_pkey PRIMARY KEY (start_date, end_date, rate);


--
-- Name: meal_rate meal_rate_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.meal_rate
    ADD CONSTRAINT meal_rate_pkey PRIMARY KEY (id);


--
-- Name: meal_tier meal_tier_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.meal_tier
    ADD CONSTRAINT meal_tier_pkey PRIMARY KEY (id, tier);


--
-- Name: travel_requestors travel_requestors_pkey; Type: CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.travel_requestors
    ADD CONSTRAINT travel_requestors_pkey PRIMARY KEY (emp_id);


--
-- Name: meal_tier meal_tier_id_meal_rate_id_f_key; Type: FK CONSTRAINT; Schema: travel; Owner: postgres
--

ALTER TABLE ONLY travel.meal_tier
    ADD CONSTRAINT meal_tier_id_meal_rate_id_f_key FOREIGN KEY (id) REFERENCES travel.meal_rate(id);


-- Completed on 2018-03-21 10:39:14 EDT

--
-- PostgreSQL database dump complete
--

--
-- Permissions
--

GRANT ALL PRIVILEGES ON SCHEMA travel TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA travel TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA travel TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA travel TO PUBLIC;
