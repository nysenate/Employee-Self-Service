
CREATE TABLE IF NOT EXISTS ess.department_head
(
    id                   SERIAL PRIMARY KEY,
    employee_id          INT NOT NULL,
    full_name            TEXT,
    department_name      TEXT,
    effective_date_range DATERANGE NOT NULL
);

INSERT INTO ess.department_head(employee_id, full_name, department_name, effective_date_range)
VALUES (12983, 'Debra R. Meade', 'Personnel', '[2023-08-01,]'::daterange),
       (12944, 'Caitlin Spinelli', 'Minority Offices', '[2023-08-01,]'::daterange),
       (58, 'Lisa P. Reid', 'Legislative Ethics', '[2023-08-01,]'::daterange),
       (7070, 'Francis J. McKearin IV', 'Maintenance & Operations', '[2023-08-01,]'::daterange),
       (10594, 'Kevin L. Crumb', 'Minority Offices', '[2023-08-01,]'::daterange),
       (7688, 'Nicholas J. Parrella', 'Student Programs', '[2023-08-01,]'::daterange),
       (7048, 'James Bell', 'Senate Technology Services', '[2023-08-01,]'::daterange),
       (9471, 'Matthew R. Lerch', 'Majority Conference Services/External Relations', '[2023-08-01,]'::daterange),
       (12729, 'Eric M. Hoppel', 'Media Services', '[2023-08-01,]'::daterange),
       (3596, 'Leslie E. King', 'Minority Offices', '[2023-08-01,]'::daterange),
       (10512, 'Michael T. Murphy', 'Majority Communications', '[2023-08-01,]'::daterange),
       (11849, 'Jordine Y. Jones', 'Senate Services', '[2023-08-01,]'::daterange),
       (13522, 'Jennifer L. Fairall', 'Legislative Library', '[2023-08-01,]'::daterange),
       (11092, 'Eric J. Katz', 'Majority Counsel/Program', '[2023-08-01,]'::daterange),
       (12696, 'Benjamin M. Sturges III', 'Sergeant at Arms', '[2023-08-01,]'::daterange),
       (3925, 'Michael C. Fallon', 'Chamber Services', '[2023-08-01,]'::daterange),
       (7885, 'Kevin T. Kather', 'Legislative Messenger Service', '[2023-08-01,]'::daterange),
       (1963, 'Frederick A. Beck', 'Senate Services', '[2023-08-01,]'::daterange),
       (6946, 'David J. Friedfel', 'Majority Finance', '[2023-08-01,]'::daterange),
       (7689, 'Alejandra N. Paulino', 'Secretary of the Senate; Majority Operations', '[2023-08-01,]'::daterange);
