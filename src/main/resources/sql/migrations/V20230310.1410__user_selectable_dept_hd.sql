-- This is part of a commit which removes the loading of departments from LDAP and instead
-- allows the user to pick their department head when submitting a travel application.

ALTER TABLE travel.app
RENAME COLUMN traveler_department_id
TO traveler_dept_head_emp_id;

DROP INDEX travel.app_traveler_department_id_index;

ALTER TABLE travel.app
ADD COLUMN IF NOT EXISTS created_date_time TIMESTAMP without TIME ZONE DEFAULT now() NOT NULL;

UPDATE travel.app
SET created_date_time = amendment.created_date_time
FROM travel.amendment
WHERE app.app_id = amendment.app_id
AND amendment.version = 'A';

CREATE INDEX ON travel.app(created_date_time);

CREATE INDEX ON travel.app(submitted_by_id);

-- Previously saved apps will no longer be valid.
TRUNCATE travel.unsubmitted_app;

ALTER TABLE travel.unsubmitted_app
ADD COLUMN IF NOT EXISTS traveler_dept_head_emp_id integer;
