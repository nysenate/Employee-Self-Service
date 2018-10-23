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
    'ACK_MANAGER',
    'SUPPLY_EMPLOYEE',
    'SUPPLY_MANAGER',
    'TIME_MANAGER',
    'SUPPLY_REPORTER'
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
--Create tables for ESS acknowledgment and set Primary Keys
--
CREATE TABLE ess.ack_doc (
    id   SERIAL PRIMARY KEY,
    title       text,
    filename    text,
    active      BOOLEAN,
    effective_date_time      TIMESTAMP without TIME ZONE
);

CREATE TABLE ess.acknowledgment (
    emp_id      INTEGER,
    ack_doc_id  INTEGER REFERENCES ess.ack_doc (id),
    timestamp   TIMESTAMP without TIME ZONE,
    personnel_acked BOOLEAN DEFAULT FALSE
);

ALTER TABLE ess.acknowledgment
    ADD PRIMARY KEY (emp_id, ack_doc_id);

-- User agent table + indices --
CREATE TABLE user_agent (
    id SERIAL PRIMARY KEY,
    login_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    emp_id INT NOT NULL,
    user_agent TEXT
);

CREATE TYPE ess.mobile_contact_options
  AS ENUM ('CALLS_ONLY', 'TEXTS_ONLY', 'EVERYTHING');

CREATE TABLE ess.alert_info (
  employee_id INT PRIMARY KEY NOT NULL,
  phone_home TEXT,
  phone_mobile TEXT,
  phone_alternate TEXT,
  mobile_options ess.mobile_contact_options NOT NULL DEFAULT 'CALLS_ONLY'::ess.mobile_contact_options,
  email_personal TEXT,
  email_alternate TEXT
);

CREATE INDEX user_agent_emp_id_user_agent_index ON user_agent(emp_id, user_agent);
CREATE INDEX user_agent_emp_id_login_time_index ON user_agent(emp_id, login_time);

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

