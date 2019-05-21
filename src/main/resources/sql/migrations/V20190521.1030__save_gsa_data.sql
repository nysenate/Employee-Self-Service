create table travel.gsa_data (
    fiscalYear int not null,
    zipcode varchar not null,
    mealTier varchar not null,
    lodgingRates varchar not null,
    city varchar,
    county varchar,
    PRIMARY KEY (fiscalYear, zipcode)
);