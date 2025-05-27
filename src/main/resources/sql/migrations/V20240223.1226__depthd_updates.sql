INSERT INTO ess.department_head(employee_id, full_name, department_name, effective_date_range)
VALUES
(6549, 'Felix Muniz', 'Fiscal Office', '[2024-01-01, 2025-01-01)'),
(14104, 'Danny Boudreau', 'Leg Health Services', '[2023-08-01,)'),
(12841, 'Jonathan Alvarenga', 'Majority Operations', '[2023-08-01,)');

UPDATE ess.department_head
SET employee_id = 12824, full_name = 'Leah Goldman', department_name = 'Majority Conference Services'
WHERE id = 8;

CREATE UNIQUE INDEX department_head_override_emp_id_dept_hd_emp_id
    ON ess.department_head_override(employee_id, department_head_emp_id);

INSERT INTO ess.department_head_override(employee_id, employee_full_name, department_head_emp_id, department_head_full_name, effective_date_range)
VALUES
    (2001, 'Dawn L. Harrington', 12944, 'Caitlin Spinelli', '[2024-01-01,)'),
    (1289, 'Joseph E. Robach', 12944, 'Caitlin Spinelli', '[2024-01-01,)'),
    (9984, 'Christopher M. Conroy', 11092, 'Eric J. Katz', '[2024-01-01,)'),
    (12785, 'Vincent R. Tilson SR', 12696, 'Benjamin M. Sturges III', '[2024-01-01,)'),
    (12787, 'Stuart A. Barksdale', 12696, 'Benjamin M. Sturges III', '[2024-01-01,)'),
    (14343, 'Abigail L. Evans', 7689, 'Alejandra N. Paulino', '[2024-01-01,)'),
    (1162, 'David J. Natoli', 7689, 'Alejandra N. Paulino', '[2024-01-01,)'),
    (8461, 'Bernadette R. Rich', 14104, 'Danny A. Boudreau', '[2024-01-01,)')