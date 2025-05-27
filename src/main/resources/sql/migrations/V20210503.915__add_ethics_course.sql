CREATE TABLE ess.ethics_course(
                                  task_id SERIAL PRIMARY KEY REFERENCES ess.personnel_task(task_id),
                                  url TEXT NOT NULL
);

COMMENT ON TABLE ess.ethics_course IS 'Detail information for the ethics course personnel task(s)';
COMMENT ON COLUMN ess.ethics_course.url IS 'URL that employees can use to access the course';

ALTER TYPE ess.personnel_task_type ADD VALUE 'ETHICS_COURSE';