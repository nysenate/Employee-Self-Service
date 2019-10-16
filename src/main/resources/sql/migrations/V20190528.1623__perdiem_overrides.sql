DELETE FROM travel.app_allowance
WHERE allowance_type IN ('MEALS', 'LODGING', 'MILEAGE');

CREATE TABLE travel.app_perdiem_override(
    app_perdiem_override_id SERIAL PRIMARY KEY,
    perdiem_type text NOT NULL,
    dollars text NOT NULL
);

CREATE TABLE travel.app_version_perdiem_override(
    app_version_perdiem_override_id SERIAL PRIMARY KEY,
    app_version_id int NOT NULL,
    app_perdiem_override_id int NOT NULL
);

ALTER TABLE travel.app_version_perdiem_override
    ADD CONSTRAINT app_version_perdiem_override_app_perdiem_override_id_fkey
    FOREIGN KEY(app_perdiem_override_id) REFERENCES travel.app_perdiem_override(app_perdiem_override_id)
    ON DELETE CASCADE;

ALTER TABLE travel.app_version_perdiem_override
    ADD CONSTRAINT app_version_perdiem_override_app_version_id_fkey
    FOREIGN KEY(app_version_id) REFERENCES travel.app_version(app_version_id)
    ON DELETE CASCADE;

-- Insert values into perdiem tables for all current application versions.

DO $$
DECLARE
    app_version_ids record;
    perdiem_id int := 0;
BEGIN
    FOR app_version_ids IN
        SELECT DISTINCT(app_version_id) FROM travel.app_version
    LOOP
        INSERT INTO travel.app_perdiem_override(perdiem_type, dollars)
        VALUES('MILEAGE', '0.00') RETURNING app_perdiem_override_id INTO perdiem_id;

        INSERT INTO travel.app_version_perdiem_override(app_version_id, app_perdiem_override_id)
        VALUES(app_version_ids.app_version_id, perdiem_id);

        INSERT INTO travel.app_perdiem_override(perdiem_type, dollars)
        VALUES('MEALS', '0.00') RETURNING app_perdiem_override_id INTO perdiem_id;

        INSERT INTO travel.app_version_perdiem_override(app_version_id, app_perdiem_override_id)
        VALUES(app_version_ids.app_version_id, perdiem_id);

        INSERT INTO travel.app_perdiem_override(perdiem_type, dollars)
        VALUES('LODGING', '0.00') RETURNING app_perdiem_override_id INTO perdiem_id;

        INSERT INTO travel.app_version_perdiem_override(app_version_id, app_perdiem_override_id)
        VALUES(app_version_ids.app_version_id, perdiem_id);
    END LOOP;
END; $$;
