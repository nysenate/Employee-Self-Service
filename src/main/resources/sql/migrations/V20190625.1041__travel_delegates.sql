CREATE TABLE travel.delegate(
    delegate_id SERIAL PRIMARY KEY,
    principal_emp_id int NOT NULL,
    delegate_emp_id int NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL
);

COMMENT ON COLUMN travel.delegate.principal_emp_id IS 'The Principal is the employee who granted permissions to a delegate employee.';
