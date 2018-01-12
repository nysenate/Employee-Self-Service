--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: ess; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA ess;


ALTER SCHEMA ess OWNER TO postgres;

SET search_path = ess, pg_catalog;

--
-- ess_role enum type
--
CREATE TYPE ess_role AS ENUM (
    'ADMIN',
    'SENATE_EMPLOYEE',
    'TIMEOUT_EXEMPT',
    'SUPPLY_EMPLOYEE',
    'SUPPLY_MANAGER'
);

ALTER TYPE ess_role OWNER TO postgres;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: user_roles; Type: TABLE; Schema: ess; Owner: postgres; Tablespace: 
--

CREATE TABLE user_roles (
    id integer NOT NULL,
    employee_id smallint NOT NULL,
    role ess_role NOT NULL
);


ALTER TABLE user_roles OWNER TO postgres;

--
-- Name: COLUMN user_roles.role; Type: COMMENT; Schema: ess; Owner: postgres
--

COMMENT ON COLUMN user_roles.role IS 'A role given to this employee. Should match a valid role in ESS backend.';


--
-- Name: user_roles_id_seq; Type: SEQUENCE; Schema: ess; Owner: postgres
--

CREATE SEQUENCE user_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_roles_id_seq OWNER TO postgres;

--
-- Name: user_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: ess; Owner: postgres
--

ALTER SEQUENCE user_roles_id_seq OWNED BY user_roles.id;


--
-- Name: id; Type: DEFAULT; Schema: ess; Owner: postgres
--

ALTER TABLE ONLY user_roles ALTER COLUMN id SET DEFAULT nextval('user_roles_id_seq'::regclass);


--
-- Name: user_roles_pkey; Type: CONSTRAINT; Schema: ess; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

--
--Create tables for ESS acknowledgement and set Primary Keys
--
CREATE TABLE ess.ack_doc (
    id   SERIAL PRIMARY KEY,
    title       text,
    filename    text,
    active      BOOLEAN,
    effective_date_time      TIMESTAMP without TIME ZONE
);

CREATE TABLE ess.acknowledgement (
    emp_id      INTEGER,
    ack_doc_id  INTEGER REFERENCES ess.ack_doc (id),
    timestamp   TIMESTAMP without TIME ZONE
);

ALTER TABLE ess.acknowledgement
    ADD PRIMARY KEY (emp_id, ack_doc_id);


--
-- Add permissions for all roles.
--
GRANT ALL PRIVILEGES ON SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON TYPE ess_role TO PUBLIC;

--
-- PostgreSQL database dump complete
--

