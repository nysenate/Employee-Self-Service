
--- Add compliance manager role, change ack manager to compliance manager.

ALTER TYPE ess.ess_role ADD VALUE 'PERSONNEL_COMPLIANCE_MANAGER';

UPDATE ess.user_roles
SET role = 'PERSONNEL_COMPLIANCE_MANAGER'::ess.ess_role
WHERE role = 'ACK_MANAGER';

--- Remove ack manager role by swapping the role enum with a new role definition.

ALTER TYPE ess.ess_role RENAME TO old_ess_role;

CREATE TYPE ess.ess_role AS ENUM (
    'ADMIN', 'SENATE_EMPLOYEE', 'SENATOR', 'TIMEOUT_EXEMPT',
    'SUPPLY_EMPLOYEE', 'SUPPLY_MANAGER', 'TIME_MANAGER', 'SUPPLY_REPORTER',
    'PERSONNEL_COMPLIANCE_MANAGER');

ALTER TABLE ess.user_roles
ALTER COLUMN role TYPE ess.ess_role USING role::text::ess.ess_role;

DROP TYPE ess.old_ess_role;
