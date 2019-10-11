CREATE TABLE travel.gsa_mie(
    gsa_mie_id SERIAL PRIMARY KEY,
    fiscal_year int NOT NULL,
    total text NOT NULL,
    breakfast text NOT NULL,
    lunch text NOT NULL,
    dinner text NOT NULL,
    incidental text NOT NULL,
    first_last_day text NOT NULL
);

CREATE UNIQUE INDEX gsa_mie_fiscal_year_total_index ON travel.gsa_mie(fiscal_year, total);
