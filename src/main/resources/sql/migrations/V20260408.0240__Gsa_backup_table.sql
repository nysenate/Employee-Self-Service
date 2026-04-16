CREATE TABLE travel.gsa_archive
(
    archive_id   SERIAL PRIMARY KEY,
    city         TEXT,
    lodgingRates VARCHAR,
    county       TEXT,
    fiscalYear   INTEGER,
    zipcode      TEXT,
    mealTier     INTEGER,

    UNIQUE (city, county, fiscalYear, zipcode)
);

CREATE INDEX gsa_archive_idx ON travel.gsa_archive (zipcode, fiscalYear);

