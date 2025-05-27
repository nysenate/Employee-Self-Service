
-- Rename personnel_employee_task to personnel_assigned_task

ALTER TABLE ess.personnel_employee_task RENAME TO personnel_assigned_task;

ALTER INDEX ess.personnel_employee_task_pkey RENAME TO personnel_assigned_task_pkey;
ALTER INDEX ess.personnel_employee_task_uk RENAME TO personnel_assigned_task_uk;

ALTER SEQUENCE ess.personnel_employee_task_id_seq RENAME TO personnel_assigned_task_id_seq;
