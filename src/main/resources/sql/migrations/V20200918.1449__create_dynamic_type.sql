ALTER TYPE ess.personnel_task_assignment_group ADD VALUE 'DYNAMIC';

create table ess.everfi_course_content_id(
        task_id SERIAL PRIMARY KEY REFERENCES ess.personnel_task(task_id),
        everfi_content_id TEXT NOT NULL
);