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

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: hstore; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;


--
-- Name: EXTENSION hstore; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION hstore IS 'data type for storing sets of (key, value) pairs';


SET search_path = supply, pg_catalog;

--
-- Name: order_status; Type: TYPE; Schema: supply; Owner: postgres
--

CREATE TYPE order_status AS ENUM (
    'APPROVED',
    'REJECTED'
);


ALTER TYPE order_status OWNER TO postgres;

--
-- Name: shipment_status; Type: TYPE; Schema: supply; Owner: postgres
--

CREATE TYPE shipment_status AS ENUM (
    'CANCELED',
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'APPROVED'
);


ALTER TYPE shipment_status OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: order; Type: TABLE; Schema: supply; Owner: postgres; Tablespace: 
--

CREATE TABLE "order" (
    order_id integer NOT NULL,
    active_version integer NOT NULL
);


ALTER TABLE "order" OWNER TO postgres;

--
-- Name: Order_order_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE "Order_order_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Order_order_id_seq" OWNER TO postgres;

--
-- Name: Order_order_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE "Order_order_id_seq" OWNED BY "order".order_id;


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
-- Name: order_history; Type: TABLE; Schema: supply; Owner: postgres; Tablespace: 
--

CREATE TABLE order_history (
    order_id integer NOT NULL,
    version_id integer NOT NULL,
    created_date_time timestamp without time zone NOT NULL
);


ALTER TABLE order_history OWNER TO postgres;

--
-- Name: COLUMN order_history.created_date_time; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN order_history.created_date_time IS 'The date time this version was created';


--
-- Name: order_version; Type: TABLE; Schema: supply; Owner: postgres; Tablespace: 
--

CREATE TABLE order_version (
    version_id integer NOT NULL,
    customer_id smallint NOT NULL,
    destination text NOT NULL,
    status order_status NOT NULL,
    note text,
    modified_by smallint NOT NULL
);


ALTER TABLE order_version OWNER TO postgres;

--
-- Name: COLUMN order_version.customer_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN order_version.customer_id IS 'The employee id of the customer. References SFMS employee tables.';


--
-- Name: COLUMN order_version.destination; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN order_version.destination IS 'The location code concatenated with ''-'' and the location type';


--
-- Name: COLUMN order_version.note; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN order_version.note IS 'Any note or comment about a order version';


--
-- Name: COLUMN order_version.modified_by; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN order_version.modified_by IS 'The employee id of whoever made this version';


--
-- Name: order_version_version_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE order_version_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE order_version_version_id_seq OWNER TO postgres;

--
-- Name: order_version_version_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE order_version_version_id_seq OWNED BY order_version.version_id;


--
-- Name: shipment; Type: TABLE; Schema: supply; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment (
    shipment_id integer NOT NULL,
    active_version_id integer NOT NULL,
    order_id integer NOT NULL
);


ALTER TABLE shipment OWNER TO postgres;

--
-- Name: COLUMN shipment.shipment_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN shipment.shipment_id IS 'The shipment id';


--
-- Name: COLUMN shipment.active_version_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN shipment.active_version_id IS 'The shipment_version id containing the current state of this shipment';


--
-- Name: COLUMN shipment.order_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN shipment.order_id IS 'The order contained in this shipment';


--
-- Name: shipment_history; Type: TABLE; Schema: supply; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment_history (
    shipment_id integer NOT NULL,
    version_id integer NOT NULL,
    created_date_time timestamp without time zone NOT NULL
);


ALTER TABLE shipment_history OWNER TO postgres;

--
-- Name: shipment_shipment_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE shipment_shipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shipment_shipment_id_seq OWNER TO postgres;

--
-- Name: shipment_shipment_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE shipment_shipment_id_seq OWNED BY shipment.shipment_id;


--
-- Name: shipment_version; Type: TABLE; Schema: supply; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment_version (
    version_id integer NOT NULL,
    issuing_emp_id smallint,
    status shipment_status NOT NULL,
    created_emp_id smallint NOT NULL
);


ALTER TABLE shipment_version OWNER TO postgres;

--
-- Name: COLUMN shipment_version.issuing_emp_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN shipment_version.issuing_emp_id IS 'The id of the issuing employee. References SFMS employee table';


--
-- Name: COLUMN shipment_version.status; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN shipment_version.status IS 'The shipment version status';


--
-- Name: COLUMN shipment_version.created_emp_id; Type: COMMENT; Schema: supply; Owner: postgres
--

COMMENT ON COLUMN shipment_version.created_emp_id IS 'The id of the employee who created this version';


--
-- Name: shipment_version_version_id_seq; Type: SEQUENCE; Schema: supply; Owner: postgres
--

CREATE SEQUENCE shipment_version_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shipment_version_version_id_seq OWNER TO postgres;

--
-- Name: shipment_version_version_id_seq; Type: SEQUENCE OWNED BY; Schema: supply; Owner: postgres
--

ALTER SEQUENCE shipment_version_version_id_seq OWNED BY shipment_version.version_id;


--
-- Name: order_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY "order" ALTER COLUMN order_id SET DEFAULT nextval('"Order_order_id_seq"'::regclass);


--
-- Name: version_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY order_version ALTER COLUMN version_id SET DEFAULT nextval('order_version_version_id_seq'::regclass);


--
-- Name: shipment_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY shipment ALTER COLUMN shipment_id SET DEFAULT nextval('shipment_shipment_id_seq'::regclass);


--
-- Name: version_id; Type: DEFAULT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY shipment_version ALTER COLUMN version_id SET DEFAULT nextval('shipment_version_version_id_seq'::regclass);


--
-- Name: Order_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "order"
    ADD CONSTRAINT "Order_pkey" PRIMARY KEY (order_id);


--
-- Name: line_item_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY line_item
    ADD CONSTRAINT line_item_pkey PRIMARY KEY (version_id, item_id);


--
-- Name: order_history_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY order_history
    ADD CONSTRAINT order_history_pkey PRIMARY KEY (order_id, version_id);


--
-- Name: order_version_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY order_version
    ADD CONSTRAINT order_version_pkey PRIMARY KEY (version_id);


--
-- Name: shipment_history_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY shipment_history
    ADD CONSTRAINT shipment_history_pkey PRIMARY KEY (shipment_id, version_id);


--
-- Name: shipment_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY shipment
    ADD CONSTRAINT shipment_pkey PRIMARY KEY (shipment_id);


--
-- Name: shipment_version_pkey; Type: CONSTRAINT; Schema: supply; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY shipment_version
    ADD CONSTRAINT shipment_version_pkey PRIMARY KEY (version_id);


--
-- Name: line_item_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY line_item
    ADD CONSTRAINT line_item_version_id_fkey FOREIGN KEY (version_id) REFERENCES order_version(version_id);


--
-- Name: order_active_version_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY "order"
    ADD CONSTRAINT order_active_version_fkey FOREIGN KEY (active_version) REFERENCES order_version(version_id);


--
-- Name: order_history_order_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY order_history
    ADD CONSTRAINT order_history_order_id_fkey FOREIGN KEY (order_id) REFERENCES "order"(order_id);


--
-- Name: order_history_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY order_history
    ADD CONSTRAINT order_history_version_id_fkey FOREIGN KEY (version_id) REFERENCES order_version(version_id);


--
-- Name: shipment_active_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY shipment
    ADD CONSTRAINT shipment_active_version_id_fkey FOREIGN KEY (active_version_id) REFERENCES shipment_version(version_id);


--
-- Name: shipment_history_shipment_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY shipment_history
    ADD CONSTRAINT shipment_history_shipment_id_fkey FOREIGN KEY (shipment_id) REFERENCES shipment(shipment_id);


--
-- Name: shipment_history_version_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY shipment_history
    ADD CONSTRAINT shipment_history_version_id_fkey FOREIGN KEY (version_id) REFERENCES shipment_version(version_id);


--
-- Name: shipment_order_id_fkey; Type: FK CONSTRAINT; Schema: supply; Owner: postgres
--

ALTER TABLE ONLY shipment
    ADD CONSTRAINT shipment_order_id_fkey FOREIGN KEY (order_id) REFERENCES "order"(order_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

