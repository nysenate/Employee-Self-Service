INSERT INTO travel.senate_mie(fiscal_year, total, breakfast, dinner)
VALUES
    (2025, '68.00', '14.00', '54.00'),
    (2025, '74.00', '15.00', '59.00'),
    (2025, '80.00', '16.00', '64.00'),
    (2025, '86.00', '17.00', '69.00'),
    (2025, '92.00', '18.00', '74.00')
    ON CONFLICT DO NOTHING;

INSERT INTO travel.senate_mie(fiscal_year, total, breakfast, dinner)
VALUES
    (2026, '68.00', '14.00', '54.00'),
    (2026, '74.00', '15.00', '59.00'),
    (2026, '80.00', '16.00', '64.00'),
    (2026, '86.00', '17.00', '69.00'),
    (2026, '92.00', '18.00', '74.00')
    ON CONFLICT DO NOTHING;
