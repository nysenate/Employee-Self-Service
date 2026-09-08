INSERT INTO travel.senate_mie(fiscal_year, total, breakfast, dinner)
VALUES
    (2027, '68.00', '14.00', '54.00'),
    (2027, '74.00', '15.00', '59.00'),
    (2027, '80.00', '16.00', '64.00'),
    (2027, '86.00', '17.00', '69.00'),
    (2027, '92.00', '18.00', '74.00')
    ON CONFLICT DO NOTHING;
