SET search_path = supply, pg_catalog;

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
-- Name: id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY location_specific_items ALTER COLUMN id SET DEFAULT nextval('location_specific_items_id_seq'::regclass);

--
-- Name: location_specific_items_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY location_specific_items
    ADD CONSTRAINT location_specific_items_pkey PRIMARY KEY (id);


--
-- Name: error_log; Type: TABLE; Schema: supply; Owner: postgres; Tablespace:
--

CREATE TABLE error_log (
    id integer NOT NULL,
    date_time timestamp without time zone DEFAULT now() NOT NULL,
    message text NOT NULL
);


ALTER TABLE error_log OWNER TO postgres;

--
-- Name: COLUMN error_log.date_time; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN error_log.date_time IS 'The date time of the error';


--
-- Name: COLUMN error_log.message; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN error_log.message IS 'The error message';


--
-- Name: error_log_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE error_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE error_log_id_seq OWNER TO postgres;

--
-- Name: error_log_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE error_log_id_seq OWNED BY error_log.id;


--
-- Name: id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY error_log ALTER COLUMN id SET DEFAULT nextval('error_log_id_seq'::regclass);


--
-- Name: error_log_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace:
--

ALTER TABLE ONLY error_log
    ADD CONSTRAINT error_log_pkey PRIMARY KEY (id);



SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = supply, pg_catalog;

--
-- Data for Name: location_specific_items; Type: TABLE DATA; Schema: supply; Owner: postgres
--

INSERT INTO location_specific_items VALUES (1, 'D04001-W', 1542);
INSERT INTO location_specific_items VALUES (2, 'D07001-W', 1542);
INSERT INTO location_specific_items VALUES (3, 'D10001-W', 1542);
INSERT INTO location_specific_items VALUES (4, 'D13001-W', 1542);
INSERT INTO location_specific_items VALUES (5, 'D14001-W', 1542);
INSERT INTO location_specific_items VALUES (6, 'D15001-W', 1542);
INSERT INTO location_specific_items VALUES (7, 'D15002-W', 1542);
INSERT INTO location_specific_items VALUES (8, 'D16001-W', 1542);
INSERT INTO location_specific_items VALUES (9, 'D18001-W', 1542);
INSERT INTO location_specific_items VALUES (10, 'D20001-W', 1542);
INSERT INTO location_specific_items VALUES (11, 'D22001-W', 1542);
INSERT INTO location_specific_items VALUES (12, 'D23002-W', 1542);
INSERT INTO location_specific_items VALUES (13, 'D24001-W', 1542);
INSERT INTO location_specific_items VALUES (14, 'D29001-W', 1542);
INSERT INTO location_specific_items VALUES (15, 'D32001-W', 1542);
INSERT INTO location_specific_items VALUES (16, 'D35001-W', 1542);
INSERT INTO location_specific_items VALUES (17, 'D36001-W', 1542);
INSERT INTO location_specific_items VALUES (18, 'D38001-W', 1542);
INSERT INTO location_specific_items VALUES (19, 'D38002-W', 1542);
INSERT INTO location_specific_items VALUES (20, 'D39001-W', 1542);
INSERT INTO location_specific_items VALUES (21, 'D40001-W', 1542);
INSERT INTO location_specific_items VALUES (22, 'D41001-W', 1542);
INSERT INTO location_specific_items VALUES (23, 'D42001-W', 1542);
INSERT INTO location_specific_items VALUES (24, 'D43002-W', 1542);
INSERT INTO location_specific_items VALUES (25, 'D48001-W', 1542);
INSERT INTO location_specific_items VALUES (26, 'D48002-W', 1542);
INSERT INTO location_specific_items VALUES (27, 'D54001-W', 1542);
INSERT INTO location_specific_items VALUES (28, 'D55001-W', 1542);
INSERT INTO location_specific_items VALUES (29, 'D56001-W', 1542);
INSERT INTO location_specific_items VALUES (30, 'D57001-W', 1542);
INSERT INTO location_specific_items VALUES (31, 'D59001-W', 1542);
INSERT INTO location_specific_items VALUES (32, 'D61001-W', 1542);
INSERT INTO location_specific_items VALUES (33, 'D61002-W', 1542);
INSERT INTO location_specific_items VALUES (34, 'D62001-W', 1542);
INSERT INTO location_specific_items VALUES (35, 'D63001-W', 1542);


--
-- Name: location_specific_items_id_seq; Type: SEQUENCE SET; Schema: supply; Owner: postgres
--

SELECT pg_catalog.setval('location_specific_items_id_seq', 35, true);
