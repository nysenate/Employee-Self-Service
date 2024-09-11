CREATE TABLE IF NOT EXISTS ess.department_head_overrides (
    id                          serial primary key,
    employee_id                 int NOT NULL,
    employee_full_name          text,
    department_head_emp_id      int NOT NULL,
    department_head_full_name   text,
    effective_date_range        daterange NOT NULL,
    notes                       text
);

INSERT INTO ess.department_head_overrides(employee_id, employee_full_name, department_head_emp_id,
                                          department_head_full_name, effective_date_range, notes)
VALUES (13815, 'Steven E. Gamache Jr.', 12668, 'Susan Rachel May', '[2023-08-01,]'::daterange, 'Department head for Rural Resources'),
       (12960, 'Corey J. Mosher', 12668, 'Susan Rachel May', '[2023-08-01,]'::daterange, 'Department head for Rural Resources');
