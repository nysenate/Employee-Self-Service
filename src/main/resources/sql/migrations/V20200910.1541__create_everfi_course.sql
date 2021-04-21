CREATE TABLE ess.everfi_course(
    task_id SERIAL PRIMARY KEY REFERENCES ess.personnel_task(task_id),
    url TEXT NOT NULL
);

COMMENT ON TABLE ess.everfi_course IS 'Detail information for everfi course personnel tasks';
COMMENT ON COLUMN ess.everfi_course.url IS 'URL that employees can use to access the course';

ALTER TYPE ess.personnel_task_type ADD VALUE 'EVERFI_COURSE';