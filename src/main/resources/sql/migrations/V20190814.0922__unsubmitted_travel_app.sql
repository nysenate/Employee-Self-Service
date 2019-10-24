-- Create a table for saving unsubmitted travel applications

CREATE TABLE travel.unsubmitted_app(
    unsubmitted_app_id SERIAL PRIMARY KEY,
    user_id int NOT NULL,
    traveler_id int NOT NULL,
    app_json text NOT NULL
);

ALTER TABLE travel.unsubmitted_app ADD CONSTRAINT unsubmited_app_user_id_traveler_id_unique UNIQUE(user_id, traveler_id);

CREATE INDEX ON travel.unsubmitted_app(user_id, traveler_id);
