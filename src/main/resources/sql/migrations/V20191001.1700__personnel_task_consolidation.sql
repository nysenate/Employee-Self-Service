
---  Personnel Task ---
-----------------------

CREATE TABLE ess.personnel_task(
    task_id SERIAL PRIMARY KEY NOT NULL,
    task_type ess.personnel_task_type NOT NULL,
    title TEXT NOT NULL,
    effective_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date_time TIMESTAMP WITHOUT TIME ZONE,
    active BOOLEAN NOT NULL
);

COMMENT ON TABLE ess.personnel_task IS 'Personnel related tasks that employees are required to complete';
COMMENT ON COLUMN ess.personnel_task.task_id IS 'Unique id for task';
COMMENT ON COLUMN ess.personnel_task.task_type IS 'Type of action required to complete the task';
COMMENT ON COLUMN ess.personnel_task.title IS 'Title/Label for the task';
COMMENT ON COLUMN ess.personnel_task.effective_date_time IS 'Time when this task becomes active';
COMMENT ON COLUMN ess.personnel_task.end_date_time IS 'Time at which this task becomes inactive';
COMMENT ON COLUMN ess.personnel_task.active IS 'Flag determining task active status.  Overrides start/end times if false.';

-- Add new task_id field to assigned tasks.  This will take the place of task type / numberl
ALTER TABLE ess.personnel_assigned_task
ADD COLUMN task_id INTEGER REFERENCES ess.personnel_task(task_id);

--- Moodle Course ---
---------------------

CREATE TABLE ess.moodle_course(
    task_id SERIAL PRIMARY KEY REFERENCES ess.personnel_task(task_id),
    url TEXT NOT NULL,
    task_number INTEGER NOT NULL -- Used to cross reference old data, will be removed in this script
);

COMMENT ON TABLE ess.moodle_course IS 'Detail information for moodle course personnel tasks';
COMMENT ON COLUMN ess.moodle_course.url IS 'URL that employees can use to access the course';

-- Insert placeholder moodle courses to task table
INSERT INTO ess.personnel_task(task_type, title, effective_date_time, end_date_time, active)
SELECT DISTINCT task_type, 'Placeholder moodle course ' || task_number,
                '1970-01-01'::TIMESTAMP WITHOUT TIME ZONE, NULL::TIMESTAMP WITHOUT TIME ZONE , TRUE
FROM ess.personnel_assigned_task
WHERE task_type = 'MOODLE_COURSE'::ess.personnel_task_type
;

--- Populate moodle course table
INSERT INTO ess.moodle_course(task_id, url, task_number)
SELECT DISTINCT t.task_id, 'https://www.example.com', task_number
FROM ess.personnel_task t
JOIN ess.personnel_assigned_task at
  ON t.task_type = at.task_type AND t.title = 'Placeholder moodle course ' || at.task_number
WHERE t.task_type = 'MOODLE_COURSE'::ess.personnel_task_type;

-- Set task id for existing moodle tasks
UPDATE ess.personnel_assigned_task t
SET task_id = c.task_id
FROM ess.moodle_course c
WHERE t.task_type = 'MOODLE_COURSE'::ess.personnel_task_type
  AND c.task_number = t.task_number
;

ALTER TABLE ess.moodle_course DROP COLUMN task_number;

--- Acknowledged Documents ---
------------------------------

-- Add acknowledgments to personnel task table and change 'ack_doc' into a detail table.

INSERT INTO ess.personnel_task(task_type, title, effective_date_time, end_date_time, active)
SELECT 'DOCUMENT_ACKNOWLEDGMENT'::ess.personnel_task_type, title,
       effective_date_time, NULL::TIMESTAMP WITHOUT TIME ZONE, active
FROM ack_doc
;

ALTER TABLE ess.ack_doc RENAME COLUMN id TO ack_doc_id;
ALTER TABLE ess.ack_doc ADD COLUMN task_id INTEGER UNIQUE REFERENCES ess.personnel_task(task_id);
UPDATE ess.ack_doc d
SET task_id = t.task_id
FROM ess.personnel_task t
WHERE t.task_type = 'DOCUMENT_ACKNOWLEDGMENT'::ess.personnel_task_type
  AND t.title = d.title
;

ALTER TABLE ess.ack_doc ALTER COLUMN task_id SET NOT NULL;

ALTER TABLE ess.ack_doc DROP COLUMN title;
ALTER TABLE ess.ack_doc DROP COLUMN active;
ALTER TABLE ess.ack_doc DROP COLUMN effective_date_time;

--- Videos ---
--------------

-- Add pec videos to personnel task table and change existing table into a detail table.

INSERT INTO ess.personnel_task(task_type, title, effective_date_time, end_date_time, active)
SELECT 'VIDEO_CODE_ENTRY'::ess.personnel_task_type, title,
       '2018-01-01'::timestamp without time zone, NULL::timestamp without time zone, active
FROM pec_video
;

ALTER TABLE ess.pec_video RENAME COLUMN id TO pec_video_id;
ALTER TABLE ess.pec_video ADD COLUMN task_id INTEGER REFERENCES ess.personnel_task(task_id);
UPDATE ess.pec_video v
SET task_id = t.task_id
FROM ess.personnel_task t
WHERE task_type = 'VIDEO_CODE_ENTRY'::ess.personnel_task_type
  AND t.title = v.title
;

ALTER TABLE ess.pec_video ALTER COLUMN task_id SET NOT NULL;

ALTER TABLE ess.pec_video DROP COLUMN title;
ALTER TABLE ess.pec_video DROP COLUMN active;

--- Task Assignments ---
------------------------
-- Finalize modifications to assigned task table

WITH task_id_map AS (
    SELECT 'DOCUMENT_ACKNOWLEDGMENT'::personnel_task_type task_type, ack_doc_id task_number, task_id
    FROM ess.ack_doc
    UNION
    SELECT 'VIDEO_CODE_ENTRY'::personnel_task_type task_type, pec_video_id task_number, task_id
    FROM ess.pec_video
)
UPDATE ess.personnel_assigned_task at
SET task_id = m.task_id
FROM task_id_map m
WHERE at.task_type = m.task_type
  AND at.task_number = m.task_number
;

ALTER TABLE ess.personnel_assigned_task ALTER COLUMN task_id SET NOT NULL;

ALTER TABLE ess.personnel_assigned_task DROP CONSTRAINT personnel_assigned_task_uk;
ALTER TABLE ess.personnel_assigned_task DROP COLUMN task_number;
ALTER TABLE ess.personnel_assigned_task DROP COLUMN task_type;

ALTER TABLE ess.personnel_assigned_task ADD CONSTRAINT personnel_assigned_task_uk UNIQUE (emp_id, task_id);

ALTER TABLE ess.personnel_assigned_task RENAME TO personnel_task_assignment;

