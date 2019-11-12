
SET SEARCH_PATH = ess;
-- Fix sequences for the recently created personnel task detail tables

ALTER TABLE moodle_course
ALTER COLUMN task_id DROP DEFAULT;

DROP SEQUENCE IF EXISTS moodle_course_task_id_seq;

SELECT setval('ess.personnel_task_task_id_seq', (SELECT COALESCE(MAX(task_id), 0) FROM personnel_task));

ALTER SEQUENCE personnel_assigned_task_id_seq RENAME TO personnel_task_assignment_id_seq;
SELECT setval('ess.personnel_task_assignment_id_seq', (SELECT COALESCE(MAX(id), 0) FROM personnel_task_assignment));

ALTER SEQUENCE policy_policy_id_seq RENAME TO ack_doc_ack_doc_id_seq;
SELECT setval('ess.ack_doc_ack_doc_id_seq', (SELECT COALESCE(MAX(ack_doc_id), 0) FROM ack_doc));

SELECT setval('ess.pec_video_id_seq', (SELECT COALESCE(MAX(pec_video_id), 0) FROM pec_video));

SELECT setval('ess.pec_video_code_id_seq', (SELECT COALESCE(MAX(id), 0) FROM pec_video_code));
