SET SEARCH_PATH = ess;

-- Adds tables to support video code entry personnel tasks.

-- Update personnel task type enum for new code entry val.

ALTER TYPE ess.personnel_task_type RENAME TO _personnel_task_type;

CREATE TYPE ess.personnel_task_type AS ENUM (
    'DOCUMENT_ACKNOWLEDGMENT',
    'MOODLE_COURSE',
    'VIDEO_CODE_ENTRY'
    );

ALTER TABLE ess.personnel_assigned_task
    ALTER COLUMN task_type TYPE ess.personnel_task_type USING task_type::text::ess.personnel_task_type;

DROP TYPE _personnel_task_type;

-- Add tables for personnel videos and code values.

CREATE TABLE ess.pec_video
(
    id       SERIAL PRIMARY KEY NOT NULL,
    title    TEXT               NOT NULL,
    filename TEXT               NOT NULL,
    active   BOOLEAN            NOT NULL
);

CREATE TABLE ess.pec_video_code
(
    id           SERIAL PRIMARY KEY NOT NULL,
    pec_video_id INTEGER            NOT NULL REFERENCES ess.pec_video (id),
    sequence_no  INTEGER            NOT NULL,
    label        TEXT               NOT NULL,
    code         TEXT               NOT NULL
);

CREATE UNIQUE INDEX pec_video_code_uk ON pec_video_code (pec_video_id, sequence_no);

