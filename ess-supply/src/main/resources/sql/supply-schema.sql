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
-- Name: supply; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA supply;


ALTER SCHEMA supply OWNER TO postgres;

SET search_path = supply, pg_catalog;

--
-- Name: requisition_status; Type: TYPE; Schema: supply; Owner: postgres
--

CREATE TYPE requisition_status AS ENUM (
    'REJECTED',
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'APPROVED'
);


ALTER TYPE requisition_status OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: requisition; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE requisition (
    requisition_id integer NOT NULL,
    active_version_id integer NOT NULL,
    ordered_date_time timestamp without time zone NOT NULL,
    processed_date_time timestamp without time zone,
    completed_date_time timestamp without time zone,
    approved_date_time timestamp without time zone,
    rejected_date_time timestamp without time zone,
    modified_date_time timestamp without time zone NOT NULL
);


ALTER TABLE requisition OWNER TO postgres;

--
-- Name: Requisition_requisition_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE "Requisition_requisition_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Requisition_requisition_id_seq" OWNER TO postgres;

--
-- Name: Requisition_requisition_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE "Requisition_requisition_id_seq" OWNED BY requisition.requisition_id;


--
-- Name: line_item; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE line_item (
    version_id integer NOT NULL,
    item_id smallint NOT NULL,
    quantity smallint NOT NULL
);


ALTER TABLE line_item OWNER TO postgres;

--
-- Name: requisition_history; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE requisition_history (
    requisition_id integer NOT NULL,
    version_id integer NOT NULL,
    created_date_time timestamp without time zone NOT NULL
);


ALTER TABLE requisition_history OWNER TO postgres;

--
-- Name: COLUMN requisition_history.created_date_time; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_history.created_date_time IS 'The date time this version was created';


--
-- Name: requisition_version; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE requisition_version (
    version_id integer NOT NULL,
    customer_id smallint NOT NULL,
    destination text NOT NULL,
    status requisition_status NOT NULL,
    issuing_emp_id smallint,
    created_emp_id smallint NOT NULL,
    note text
);


ALTER TABLE requisition_version OWNER TO postgres;

--
-- Name: COLUMN requisition_version.customer_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_version.customer_id IS 'The employee id of the customer. References SFMS employee tables.';


--
-- Name: COLUMN requisition_version.destination; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_version.destination IS 'The location code concatenated with ''-'' and the location type';


--
-- Name: COLUMN requisition_version.created_emp_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_version.created_emp_id IS 'The employee id of whoever made this version';


--
-- Name: COLUMN requisition_version.note; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_version.note IS 'Any note or comment about a requisition version';


--
-- Name: requisition_version_version_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE requisition_version_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisition_version_version_id_seq OWNER TO postgres;

--
-- Name: requisition_version_version_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE requisition_version_version_id_seq OWNED BY requisition_version.version_id;


--
-- Name: requisition_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY requisition ALTER COLUMN requisition_id SET DEFAULT nextval('"Requisition_requisition_id_seq"'::regclass);


--
-- Name: version_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY requisition_version ALTER COLUMN version_id SET DEFAULT nextval('requisition_version_version_id_seq'::regclass);


--
-- Name: Requisition_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY requisition
    ADD CONSTRAINT "Requisition_pkey" PRIMARY KEY (requisition_id);


--
-- Name: line_item_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY line_item
    ADD CONSTRAINT line_item_pkey PRIMARY KEY (version_id, item_id);


--
-- Name: requisition_history_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY requisition_history
    ADD CONSTRAINT requisition_history_pkey PRIMARY KEY (requisition_id, version_id);


--
-- Name: requisition_version_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY requisition_version
    ADD CONSTRAINT requisition_version_pkey PRIMARY KEY (version_id);


--
-- Name: line_item_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY line_item
    ADD CONSTRAINT line_item_version_id_fkey FOREIGN KEY (version_id) REFERENCES requisition_version(version_id);


--
-- Name: requisition_active_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY requisition
    ADD CONSTRAINT requisition_active_version_id_fkey FOREIGN KEY (active_version_id) REFERENCES requisition_version(version_id);


--
-- Name: requisition_history_requisition_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY requisition_history
    ADD CONSTRAINT requisition_history_requisition_id_fkey FOREIGN KEY (requisition_id) REFERENCES requisition(requisition_id);


--
-- Name: requisition_history_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY requisition_history
    ADD CONSTRAINT requisition_history_version_id_fkey FOREIGN KEY (version_id) REFERENCES requisition_version(version_id);


