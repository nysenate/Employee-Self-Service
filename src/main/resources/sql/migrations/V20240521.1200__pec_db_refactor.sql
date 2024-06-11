ALTER TABLE ess.personnel_task
    ADD column url text;
ALTER TABLE ess.personnel_task
    ADD column resource text;


UPDATE ess.personnel_task
SET url = ethics_live_course.url FROM ess.ethics_live_course
WHERE ethics_live_course.task_id = personnel_task.task_id;

UPDATE ess.personnel_task
SET url = ethics_course.url FROM ess.ethics_course
WHERE ethics_course.task_id = personnel_task.task_id;

UPDATE ess.personnel_task
SET url = moodle_course.url FROM ess.moodle_course
WHERE moodle_course.task_id = personnel_task.task_id;

UPDATE ess.personnel_task
SET url = everfi_course.url FROM ess.everfi_course
WHERE everfi_course.task_id = personnel_task.task_id;

UPDATE ess.personnel_task
SET resource = ack_doc.filename FROM ess.ack_doc
WHERE ack_doc.task_id = personnel_task.task_id;

UPDATE ess.personnel_task
SET resource = pec_video.filename FROM ess.pec_video
WHERE pec_video.task_id = personnel_task.task_id;


alter table ess.ethics_code add column task_id integer;

UPDATE ess.ethics_code
SET task_id = ethics_live_course.task_id FROM ess.ethics_live_course
WHERE ethics_live_course.ethics_code_id = ethics_code.ethics_code_id;

alter table ess.ethics_code drop constraint ethics_code_ethics_code_id_fkey;
alter table ess.ethics_code drop column ethics_code_id;

alter table ess.acknowledgment add column task_id integer;

UPDATE ess.acknowledgment
SET task_id = ack_doc.task_id FROM ess.ack_doc
WHERE ack_doc.ack_doc_id = acknowledgment.ack_doc_id;

alter table ess.acknowledgment drop constraint acknowledgement_ack_doc_id_fkey;
alter table ess.acknowledgment drop constraint acknowledgement_pkey;
alter table ess.acknowledgment add PRIMARY KEY (emp_id, task_id);
alter table ess.acknowledgment drop column ack_doc_id;

alter table ess.pec_video_code add column task_id integer;

UPDATE ess.pec_video_code
SET task_id = pec_video.task_id from ess.pec_video
WHERE pec_video.pec_video_id = pec_video_code.pec_video_id;

alter table ess.pec_video_code drop constraint pec_video_code_pec_video_id_fkey;
alter table ess.pec_video_code drop column pec_video_id;


drop table ess.ethics_live_course;
drop table ess.ack_doc;
drop table ess.pec_video;
drop table ess.ethics_course;
drop table ess.moodle_course;
drop table ess.everfi_course;