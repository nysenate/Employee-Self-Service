-- Adds a table for mapping a users employee id to additional RCH's that user can submit travel application for.

CREATE TABLE travel.user_orderable_rch(
    user_orderable_rch_id SERIAL PRIMARY KEY,
    emp_id int NOT NULL,
    rch text NOT NULL,
    created_date_time timestamp with time zone NOT NULL default now()
);

comment on table travel.user_orderable_rch is 'Maps RCHs to an employee, allowing the employee to submit travel applications for all employees in the given RCH.';
comment on column travel.user_orderable_rch.rch is 'The Responsibility Center Head code';

CREATE UNIQUE INDEX user_orderable_rch_emp_id_rch_index ON travel.user_orderable_rch(emp_id, rch);
