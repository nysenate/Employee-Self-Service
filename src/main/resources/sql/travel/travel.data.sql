--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 10.3

-- Started on 2018-03-21 10:48:05 EDT

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
-- Data for Name: irs_mileage_rate; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.irs_mileage_rate (start_date, end_date, rate) FROM stdin;
2018-01-01	2018-12-31	0.545
\.


--
-- Data for Name: meal_rate; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.meal_rate (id, start_date, end_date) FROM stdin;
4ee5c6d8-ac93-456f-b5f8-3ec970be4b66 2018-01-05	\N
\.


--
-- Data for Name: meal_tier; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.meal_tier (id, tier, breakfast, lunch, dinner, incidental) FROM stdin;
3eb3ad52-15f9-449a-ba19-c17b71dc201c 4ee5c6d8-ac93-456f-b5f8-3ec970be4b66 69	16	17	31	5
bfd62f90-6fc0-4563-a74f-a8a8d5beacc4 4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	59	13	15	26	5
b40c2f51-1c9d-4257-a2dc-0409d20aadd1 4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	51	11	12	23	5
dd66109f-fddc-490a-8e65-72f4d03c52e2 4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	74	17	18	34	5
45b7f334-f19e-41d9-b32a-8d4b88c94a04 4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	64	15	16	28	5
8cc8c148-5398-4b06-936f-22fd73591b77 4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	54	12	13	24	5
\.


--
-- Data for Name: travel_requestors; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.travel_requestors (emp_id, requestor_id, start_date, end_date) FROM stdin;
\.


--
-- Name: meal_rate_id_seq; Type: SEQUENCE SET; Schema: travel; Owner: postgres
--

SELECT pg_catalog.setval('travel.meal_rate_id_seq', 1, true);


-- Completed on 2018-03-21 10:48:05 EDT

--
-- PostgreSQL database dump complete
--
