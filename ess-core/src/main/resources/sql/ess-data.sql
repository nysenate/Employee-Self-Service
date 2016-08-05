--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = ess, pg_catalog;

--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: ess; Owner: postgres
--

INSERT INTO user_roles VALUES (1, 9881, 'SUPPLY_EMPLOYEE');
INSERT INTO user_roles VALUES (2, 9881, 'SUPPLY_MANAGER');
INSERT INTO user_roles VALUES (3, 9881, 'TIMEOUT_EXEMPT');
INSERT INTO user_roles VALUES (4, 11591, 'SUPPLY_EMPLOYEE');
INSERT INTO user_roles VALUES (5, 11591, 'SUPPLY_MANAGER');
INSERT INTO user_roles VALUES (6, 11591, 'TIMEOUT_EXEMPT');
INSERT INTO user_roles VALUES (7, 9322, 'SUPPLY_EMPLOYEE');
INSERT INTO user_roles VALUES (8, 9322, 'TIMEOUT_EXEMPT');
INSERT INTO user_roles VALUES (9, 9956, 'SUPPLY_EMPLOYEE');
INSERT INTO user_roles VALUES (10, 9956, 'TIMEOUT_EXEMPT');


--
-- Name: user_roles_id_seq; Type: SEQUENCE SET; Schema: ess; Owner: postgres
--

SELECT pg_catalog.setval('user_roles_id_seq', 10, true);


--
-- PostgreSQL database dump complete
--
