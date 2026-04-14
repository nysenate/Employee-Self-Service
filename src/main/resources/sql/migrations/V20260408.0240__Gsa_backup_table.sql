CREATE TABLE travel.gsa_archive
(
    archive_id   SERIAL PRIMARY KEY,
    city         TEXT,
    lodgingRates VARCHAR,
    county       TEXT,
    fiscalYear   INTEGER,
    zipcode      INTEGER,
    mealTier     INTEGER,
    source_file  TEXT,

    UNIQUE (city, county, fiscalYear, zipcode, source_file)
);

CREATE INDEX gsa_archive_idx ON travel.gsa_archive (city, zipcode, fiscalYear);

