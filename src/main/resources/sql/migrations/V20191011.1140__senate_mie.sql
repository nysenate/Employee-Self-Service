CREATE TABLE travel.senate_mie(
    senate_mie_id SERIAL PRIMARY KEY,
    fiscal_year int NOT NULL,
    total text NOT NULL,
    breakfast text NOT NULL,
    dinner text NOT NULL
);

CREATE UNIQUE INDEX senate_mie_fiscal_year_total_index ON travel.senate_mie(fiscal_year, total);

INSERT INTO travel.senate_mie(fiscal_year, total, breakfast, dinner)
VALUES
(2020, '55.00', '11.00', '44.00'),
(2020, '56.00', '11.00', '45.00'),
(2020, '61.00', '12.00', '49.00'),
(2020, '66.00', '13.00', '53.00'),
(2020, '71.00', '14.00', '57.00'),
(2020, '76.00', '15.00', '61.00');