CREATE TABLE travel.app_version_status(
    app_version_status_id SERIAL PRIMARY KEY,
    app_version_id int NOT NULL,
    created_date_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    status text NOT NULL,
    note text
);

ALTER TABLE travel.app_version_status
    ADD CONSTRAINT app_version_status_app_version_id_fkey FOREIGN KEY (app_version_id)
    REFERENCES travel.app_version(app_version_id)
    ON DELETE CASCADE;

-- Initialize the app_version_status table with statuses stored in app_version.
INSERT INTO travel.app_version_status(app_version_id, status)
SELECT app_version_id, status FROM travel.app_version;


-- Drop the status column from app_version
ALTER TABLE travel.app_version
DROP COLUMN status;