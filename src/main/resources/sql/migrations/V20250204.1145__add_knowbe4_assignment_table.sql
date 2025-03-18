create table ess.knowbe4_course_assignment_id(
                                                 knowbe4_assignment_id INTEGER PRIMARY KEY,
                                                 task_id SERIAL REFERENCES ess.personnel_task(task_id)

);

alter type ess.personnel_task_type add VALUE 'KNOWBE4_COURSE';