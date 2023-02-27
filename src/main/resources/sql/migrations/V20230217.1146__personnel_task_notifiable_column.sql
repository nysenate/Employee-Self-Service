--Notifyable column for tasks themselves
alter table ess.personnel_task add column "notifiable" BOOLEAN NOT NULL DEFAULT FALSE;