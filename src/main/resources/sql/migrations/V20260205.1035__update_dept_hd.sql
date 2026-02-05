-- Update the department head for majority conference services
UPDATE ess.department_head
SET effective_date_range = daterange(lower(effective_date_range), '2026-01-29'::date)
WHERE employee_id = 12824;

INSERT INTO ess.department_head(employee_id, full_name, department_name, effective_date_range)
VALUES (13472, 'Emily Bruggeman', 'Majority Conference Services', daterange('2026-01-29', 'infinity'::date))
ON CONFLICT DO NOTHING;
