SET SEARCH_PATH = ess;

-- Remove personnel manager role, replacing it with time and ack manager
-- Set all existing personnel managers to time managers

ALTER TYPE ess_role ADD VALUE 'TIME_MANAGER';

UPDATE user_roles
SET role = 'TIME_MANAGER'::ess_role
WHERE role = 'PERSONNEL_MANAGER'::ess_role
;

ALTER TYPE ess_role RENAME TO ess_role_old;

CREATE TYPE ess_role AS ENUM (
  'ADMIN',
  'SENATE_EMPLOYEE',
  'TIMEOUT_EXEMPT',
  'ACK_MANAGER',
  'SUPPLY_EMPLOYEE',
  'SUPPLY_MANAGER',
  'TIME_MANAGER'
);

ALTER TABLE user_roles
ALTER COLUMN role
TYPE ess_role
USING role::text::ess_role
;

DROP TYPE ess_role_old;
