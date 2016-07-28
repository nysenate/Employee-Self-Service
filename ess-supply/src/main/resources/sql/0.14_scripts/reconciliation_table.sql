--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = supply, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: reconciliation_category_groups; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE reconciliation_category_groups (
    id integer NOT NULL,
    item_category text NOT NULL,
    page smallint NOT NULL
);


ALTER TABLE reconciliation_category_groups OWNER TO postgres;

--
-- Name: TABLE reconciliation_category_groups; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON TABLE reconciliation_category_groups IS 'Groups supply item categories to the reconciliation page they should be displayed on.';


--
-- Name: COLUMN reconciliation_category_groups.item_category; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN reconciliation_category_groups.item_category IS 'The supply item category';


--
-- Name: COLUMN reconciliation_category_groups.page; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN reconciliation_category_groups.page IS 'The reconciliation page this item should be on.';


--
-- Name: reconciliation_category_groups_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE reconciliation_category_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE reconciliation_category_groups_id_seq OWNER TO postgres;

--
-- Name: reconciliation_category_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE reconciliation_category_groups_id_seq OWNED BY reconciliation_category_groups.id;


--
-- Name: id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY reconciliation_category_groups ALTER COLUMN id SET DEFAULT nextval('reconciliation_category_groups_id_seq'::regclass);


--
-- Name: reconciliation_category_groups_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY reconciliation_category_groups
    ADD CONSTRAINT reconciliation_category_groups_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

GRANT ALL PRIVILEGES ON SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA supply TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA supply TO PUBLIC;