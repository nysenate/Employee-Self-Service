INSERT INTO travel.senate_mie(fiscal_year, total, breakfast, dinner)
VALUES
    (2024, '59.00', '12.00', '47.00'),
    (2024, '64.00', '13.00', '51.00'),
    (2024, '69.00', '14.00', '55.00'),
    (2024, '74.00', '15.00', '59.00'),
    (2024, '79.00', '16.00', '63.00')
    ON CONFLICT DO NOTHING;