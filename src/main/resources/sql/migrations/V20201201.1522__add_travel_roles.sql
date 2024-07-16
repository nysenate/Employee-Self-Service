
-- Add new enum types
ALTER TYPE ess.ess_role ADD VALUE 'TRAVEL_ADMIN';
ALTER TYPE ess.ess_role ADD VALUE 'SECRETARY_OF_SENATE';
COMMIT;

-- Insert Employees
INSERT INTO ess.user_roles(employee_id, role)
VALUES (7689, 'SECRETARY_OF_SENATE'),
       (4626, 'TRAVEL_ADMIN')
ON CONFLICT DO NOTHING;
