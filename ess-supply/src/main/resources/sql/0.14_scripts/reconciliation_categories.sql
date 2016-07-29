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



SET search_path = supply, pg_catalog;

Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BADGES', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BATTERIES', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BINDERS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/PUNCH', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BULLETIN/BOARDS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('CALCULATOR/ACC', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BOOKS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('CALENDARS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('DIARIES', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('CARDBOARD', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('CLIPS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('COMP/ACCESS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/COPIER', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/LOOSELEAF', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/STATIONER', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('KORECTYPE', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('CUPS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BLOTTERS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BOOKENDS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('COMP/MOUSE', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('COPYHOLDERS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('DESK/ORGANIZER', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/ENVELOPES', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('FILES', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('GLUE', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('CARDS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('FAX/SUPPLIES', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('LABELS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BULBS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('COVERS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('DOOR/ACCESS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('LETTER/OPENER', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('RUBBERBAND', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('RULERS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('SCISSORS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('WASTE/REC', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('POST-IT', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PADS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/PADS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PAPER/GOODS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('MOISTENERS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('POSTAL/ACCESS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('ROLODEX', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('SEAL', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('INK/PAD', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('STAPLERS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('TAPES', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('BAGS', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('MARKERS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PENCILS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('PENS', 1);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('COMP/PR/RIBBON', 2);
Insert into supply.reconciliation_category_groups(item_category, page) VALUES ('TYPEWR/ACCESS', 2);
