ALTER TABLE ess.personnel_task
    ADD column url text;
ALTER TABLE ess.personnel_task
    ADD column resource text;

alter table ess.ethics_code drop constraint ethics_code_ethics_code_id_fkey;
alter table ess.ethics_code rename ethics_code_id to task_id;

alter table ess.acknowledgment drop constraint acknowledgement_ack_doc_id_fkey;
alter table ess.acknowledgment add column task_id integer;
alter table ess.acknowledgment drop constraint acknowledgement_pkey;
alter table ess.acknowledgment add PRIMARY KEY (emp_id, task_id);

alter table ess.pec_video_code add column task_id integer;
alter table ess.pec_video_code drop constraint pec_video_code_pec_video_id_fkey;

drop table ess.ethics_live_course;
drop table ess.ack_doc;
drop table ess.pec_video;
drop table ess.ethics_course;
drop table ess.moodle_course;
drop table ess.everfi_course;
