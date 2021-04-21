
-- Adds an 'active' column to the personnel assigned task table allowing tasks to be removed.

-- Default to true for all existing tasks
ALTER TABLE ess.personnel_assigned_task
ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

-- Drop default value after existing tasks set.
ALTER TABLE ess.personnel_assigned_task
ALTER COLUMN active DROP DEFAULT;
