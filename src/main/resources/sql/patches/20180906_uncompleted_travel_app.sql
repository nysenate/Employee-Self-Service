CREATE TABLE travel.uncompleted_travel_application
(
    id uuid PRIMARY KEY NOT NULL,
    version_id uuid NOT NULL,
    traveler_id int NOT NULL,
    submitter_id int NOT NULL,
    app_json text NOT NULL,
    modified_date_time timestamp without time zone DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX uncompleted_travel_application_traveler_id_uindex ON travel.uncompleted_travel_application (traveler_id);
