
-- Add an enum and table field to specify custom assignment logic for personnel tasks.

CREATE TYPE ess.personnel_task_assignment_group AS ENUM (
    'DEFAULT',
    'ETHICS'
);

ALTER TABLE ess.personnel_task
ADD COLUMN assignment_group
    ess.personnel_task_assignment_group NOT NULL DEFAULT 'DEFAULT'::ess.personnel_task_assignment_group
;

ALTER TABLE ess.personnel_task ALTER COLUMN assignment_group DROP DEFAULT;
