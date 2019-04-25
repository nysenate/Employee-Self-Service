
--- Creates tables to store personnel employee tasks.

CREATE TYPE ess.personnel_task_type AS ENUM (
    'DOCUMENT_ACKNOWLEDGMENT',
    'ETHICS_ORIENTATION',
    'ETHICS_REVIEW',
    'HARASSMENT_TRAINING'
);

CREATE TABLE ess.personnel_employee_task (
    id SERIAL NOT NULL PRIMARY KEY,
    emp_id INTEGER NOT NULL,
    task_type ess.personnel_task_type NOT NULL,
    task_id INTEGER,
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    update_user_id INTEGER,
    completed BOOLEAN NOT NULL
);

ALTER TABLE ess.personnel_employee_task
    ADD CONSTRAINT personnel_employee_task_uk UNIQUE (emp_id, task_type, task_id);
