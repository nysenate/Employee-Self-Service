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
-- Name: line_item; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE line_item (
  revision_id integer NOT NULL,
  item_id smallint NOT NULL,
  quantity smallint NOT NULL
);


ALTER TABLE line_item OWNER TO postgres;

--
-- Name: location_specific_items; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE location_specific_items (
  id integer NOT NULL,
  location_id text NOT NULL,
  item_id integer NOT NULL
);


ALTER TABLE location_specific_items OWNER TO postgres;

--
-- Name: TABLE location_specific_items; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON TABLE location_specific_items IS 'An inclusive list of locations and items that the location is allowed to order. If an item is in any row in this table, it can only be ordered by locations specified in this table. If an item is not specified in any row here, it can be ordered from any location.';


--
-- Name: location_specific_items_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE location_specific_items_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE location_specific_items_id_seq OWNER TO postgres;

--
-- Name: location_specific_items_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE location_specific_items_id_seq OWNED BY location_specific_items.id;


--
-- Name: requisition; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE requisition (
  requisition_id integer NOT NULL,
  current_revision_id integer NOT NULL,
  ordered_date_time timestamp without time zone NOT NULL,
  processed_date_time timestamp without time zone,
  completed_date_time timestamp without time zone,
  approved_date_time timestamp without time zone,
  rejected_date_time timestamp without time zone,
  last_sfms_sync_date_time timestamp without time zone,
  saved_in_sfms boolean NOT NULL
);


ALTER TABLE requisition OWNER TO postgres;

--
-- Name: requisition_content; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE requisition_content (
  requisition_id integer NOT NULL,
  revision_id integer NOT NULL,
  destination text NOT NULL,
  special_instructions text,
  status requisition_status NOT NULL,
  issuing_emp_id smallint,
  note text,
  customer_id smallint NOT NULL,
  modified_by_id smallint NOT NULL,
  modified_date_time timestamp without time zone NOT NULL
);


ALTER TABLE requisition_content OWNER TO postgres;

--
-- Name: COLUMN requisition_content.destination; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_content.destination IS 'The location code concatenated with ''-'' and the location type';


--
-- Name: COLUMN requisition_content.note; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_content.note IS 'Any note or comment about this requisition';


--
-- Name: COLUMN requisition_content.modified_by_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN requisition_content.modified_by_id IS 'The employee id of the employee who saved this modification';


--
-- Name: requisition_content_revision_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE requisition_content_revision_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE requisition_content_revision_id_seq OWNER TO postgres;

--
-- Name: requisition_content_revision_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE requisition_content_revision_id_seq OWNED BY requisition_content.revision_id;


--
-- Name: requisition_requisition_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE requisition_requisition_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


ALTER TABLE requisition_requisition_id_seq OWNER TO postgres;

--
-- Name: requisition_requisition_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE requisition_requisition_id_seq OWNED BY requisition.requisition_id;


--
-- Name: id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY location_specific_items ALTER COLUMN id SET DEFAULT nextval('location_specific_items_id_seq'::regclass);


--
-- Name: requisition_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY requisition ALTER COLUMN requisition_id SET DEFAULT nextval('requisition_requisition_id_seq'::regclass);


--
-- Name: line_item_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY line_item
  ADD CONSTRAINT line_item_pkey PRIMARY KEY (revision_id, item_id);


--
-- Name: location_specific_items_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY location_specific_items
  ADD CONSTRAINT location_specific_items_pkey PRIMARY KEY (id);


--
-- Name: requisition_content_revision_id_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY requisition_content
  ADD CONSTRAINT requisition_content_revision_id_pkey PRIMARY KEY (revision_id);


--
-- Name: requisition_current_revision_id_unique; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY requisition
  ADD CONSTRAINT requisition_current_revision_id_unique UNIQUE (current_revision_id);


--
-- Name: requisition_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY requisition
  ADD CONSTRAINT requisition_pkey PRIMARY KEY (requisition_id);


--
-- Name: line_item_requisition_content_revision_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY line_item
  ADD CONSTRAINT line_item_requisition_content_revision_id_fkey FOREIGN KEY (revision_id) REFERENCES requisition_content(revision_id);


--
-- Permissions
--

GRANT ALL PRIVILEGES ON SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON TYPE requisition_status TO PUBLIC;
