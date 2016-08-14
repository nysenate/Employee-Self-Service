SET search_path = ess, pg_catalog;

CREATE TYPE ess_role AS ENUM (
  'ADMIN',
  'SENATE_EMPLOYEE',
  'TIMEOUT_EXEMPT',
  'SUPPLY_EMPLOYEE',
  'SUPPLY_MANAGER'
);

ALTER TYPE ess_role OWNER TO postgres;

GRANT ALL PRIVILEGES ON TYPE ess_role TO PUBLIC;

ALTER TABLE user_roles ALTER role TYPE ess_role USING role::ess_role;
