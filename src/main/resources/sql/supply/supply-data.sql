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
