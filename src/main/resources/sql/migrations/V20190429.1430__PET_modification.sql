
--- Replace initial personnel task types with more generic variants.

ALTER TYPE ess.personnel_task_type RENAME TO _personnel_task_type;

CREATE TYPE ess.personnel_task_type AS ENUM (
    'DOCUMENT_ACKNOWLEDGMENT',
    'MOODLE_COURSE',
    'CODE_ENTRY'
);

ALTER TABLE ess.personnel_employee_task
    ALTER COLUMN task_type TYPE ess.personnel_task_type USING task_type::text::ess.personnel_task_type;

DROP TYPE _personnel_task_type;

-- Rename task id to task_number to reflect java model.

ALTER TABLE ess.personnel_employee_task RENAME COLUMN task_id TO task_number;

--- Add not null constraint to the task number.

ALTER TABLE ess.personnel_employee_task ALTER COLUMN task_number SET NOT NULL;
