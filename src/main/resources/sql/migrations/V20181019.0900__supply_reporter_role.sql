ALTER TYPE ess.ess_role ADD VALUE 'SUPPLY_REPORTER';

INSERT INTO ess.user_roles(employee_id, role)
VALUES (8440, 'SUPPLY_REPORTER'),
       (884, 'SUPPLY_REPORTER');