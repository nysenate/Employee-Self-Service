CREATE TABLE IF NOT EXISTS ess.department(
    department_id serial primary key,
    name          text unique,
    head_emp_id   int,
    is_active     boolean default true
);

CREATE TABLE IF NOT EXISTS ess.employee_department(
    employee_department_id serial primary key,
    department_id          int references ess.department(department_id),
    employee_id            int unique
);

ALTER TABLE travel.app
ADD COLUMN traveler_department_id int;


-- Insert initial department heads

INSERT INTO ess.department (name, head_emp_id)
VALUES
('Chamber Services', 3925),
('CH/Document Room', 3925),
('CH/Journal Clerk''s Office', 3925),
('FO/Accounts Payable', 231),
('FO/Payroll', 231),
('Senate Fiscal Office', 7070),
('M&O/CUSTODIAL SER', 7070),
('M&O/Director''s Office', 7070),
('M&O/DO Coordinator', 7070),
('M&O/Furnishings Control', 7070),
('M&O/Inv & Records Management', 7070),
('M&O/Maintenance', 7070),
('M&O/Post Office', 7070),
('M&O/Purchasing', 7070),
('M&O/Receiving', 7070),
('M&O/Shop', 7070),
('M&O/Supply', 7070),
('M&O/Telephone Operators', 7070),
('Majority Communications', 10512),
('Majority Conference Services', 11087),
('Majority Counsel/Program', 9640),
('Senate Finance Committee/Major', 12664),
('Majority Operations', 7689),
('Majority Senior Staff', 7689),
('Media Services', 12729),
('Senate Personnel Office', 12983),
('SS/Production Services', 1963),
('SS/Graphic Arts', 1963),
('Senate Technology Services', 7048),
('STS/Business Applications Center', 7048),
('STS/Telecommunications', 7048),
('Senate Sergeant-At-Arms', 7689),
('Student Programs Office', 7688),
('Student Programs/Fellows', 7688),
('Minority Administration', 2001),
('Minority Communications', 2001),
('Minority Conference Services', 2001),
('Minority Counsel/Program', 2001),
('Senate Finance Committee/Minor', 2001),
('Minority Senior Staff', 2001),
('Legislative Library', 2350),
('Legislative Messenger Service', 7885),
('LC/Legislative Ethics Commission', 58);
