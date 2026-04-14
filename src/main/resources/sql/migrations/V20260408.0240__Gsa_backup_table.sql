CREATE TABLE travel.gsa_archive
(
    archive_id   SERIAL PRIMARY KEY,
    city         TEXT,
    lodgingRates VARCHAR,
    county       TEXT,
    fiscalYear   INTEGER,
    zipcode      INTEGER,
    mealTier     INTEGER,

    UNIQUE (city, county, fiscalYear, zipcode)
);

CREATE INDEX gsa_archive_idx ON travel.gsa_archive (city, zipcode, fiscalYear);

