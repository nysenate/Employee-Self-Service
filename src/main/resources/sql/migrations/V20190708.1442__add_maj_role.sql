ALTER TYPE ess.ess_role ADD VALUE IF NOT EXISTS 'MAJORITY_LEADER';

INSERT INTO ess.user_roles(employee_id, role)
VALUES (8944, 'MAJORITY_LEADER')
ON CONFLICT DO NOTHING;
