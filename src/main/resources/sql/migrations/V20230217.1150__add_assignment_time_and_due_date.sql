--Assignment due date and assignemnt date
alter table ess.personnel_task_assignment add column "assignment_date" timestamp default null;
alter table ess.personnel_task_assignment add column "due_date" timestamp default null;