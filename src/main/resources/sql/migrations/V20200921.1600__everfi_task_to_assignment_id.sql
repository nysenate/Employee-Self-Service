create table ess.everfi_course_assignment_id(
       everfi_assignment_id INTEGER PRIMARY KEY,
       task_id SERIAL REFERENCES ess.personnel_task(task_id)

);