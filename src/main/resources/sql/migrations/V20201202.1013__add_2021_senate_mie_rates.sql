INSERT INTO travel.senate_mie(fiscal_year, total, breakfast, dinner)
VALUES
(2021, '55.00', '11.00', '44.00'),
(2021, '56.00', '11.00', '45.00'),
(2021, '61.00', '12.00', '49.00'),
(2021, '66.00', '13.00', '53.00'),
(2021, '71.00', '14.00', '57.00'),
(2021, '76.00', '15.00', '61.00')
ON CONFLICT DO NOTHING;