ALTER TYPE ess.personnel_task_type ADD VALUE 'ETHICS_LIVE_COURSE';

Create table ess.ethics_live_course (
                                        task_id integer UNIQUE NOT NULL PRIMARY KEY REFERENCES ess.personnel_task(task_id),
                                        url text,
                                        ethics_code_id integer UNIQUE );
create table ess.ethics_code(
                                id SERIAL PRIMARY KEY,
                                ethics_code_id integer references ess.ethics_live_course(ethics_code_id),
                                sequence_no integer,
                                label text,
                                code text
);