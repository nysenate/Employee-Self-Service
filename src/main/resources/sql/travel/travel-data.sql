--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5
-- Dumped by pg_dump version 10.5

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
-- Data for Name: meal_rate; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.meal_rate (id, start_date, end_date) FROM stdin;
4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	2018-01-05	2999-01-01
\.


--
-- Data for Name: meal_tier; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.meal_tier (id, meal_rate_id, tier, incidental, total) FROM stdin;
b40c2f51-1c9d-4257-a2dc-0409d20aadd1	4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	51	5	46
8cc8c148-5398-4b06-936f-22fd73591b77	4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	54	5	49
bfd62f90-6fc0-4563-a74f-a8a8d5beacc4	4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	59	5	54
45b7f334-f19e-41d9-b32a-8d4b88c94a04	4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	64	5	59
3eb3ad52-15f9-449a-ba19-c17b71dc201c	4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	69	5	64
dd66109f-fddc-490a-8e65-72f4d03c52e2	4ee5c6d8-ac93-456f-b5f8-3ec970be4b66	74	5	69
\.


--
-- Data for Name: irs_mileage_rate; Type: TABLE DATA; Schema: travel; Owner: postgres
--

COPY travel.irs_mileage_rate (start_date, end_date, rate) FROM stdin;
2018-01-01	2018-12-31	0.545
\.
