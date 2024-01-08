----------------------------
-- ** Refactor Allowances **
----------------------------

-- Delete inactive allowances.
DELETE
FROM travel.amendment_allowances
WHERE amendment_id NOT IN (
    SELECT max(amendment_id) as curramd
    FROM travel.amendment
    GROUP BY app_id
);

DELETE
FROM travel.allowance
WHERE allowance_id NOT IN (
    SELECT allowance_id
    FROM travel.amendment_allowances
);

-- Add FK to app_id
ALTER TABLE travel.allowance
    ADD COLUMN app_id int;

-- Initialize app_id column.
UPDATE travel.allowance
SET app_id = amendment.app_id FROM travel.amendment
         INNER JOIN travel.amendment_allowances USING (amendment_id)
WHERE allowance.allowance_id = amendment_allowances.allowance_id;

-- Delete old records for non current amendment versions
DELETE
FROM travel.allowance
WHERE app_id is null;

ALTER TABLE travel.amendment_allowances
DROP
CONSTRAINT IF EXISTS amendment_allowances_amendment_allowances_id_fkey;

DROP TABLE IF EXISTS travel.amendment_allowances;

ALTER TABLE travel.allowance
    RENAME TO app_allowance;

ALTER TABLE travel.app_allowance
    ADD CONSTRAINT app_allowance_app_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id);

ALTER TABLE travel.app_allowance
    ALTER COLUMN app_id SET NOT NULL;

CREATE UNIQUE INDEX ON travel.app_allowance (app_id, type);


----------------------------
-- ** Refactor Attachments **
----------------------------

-- Delete inactive attachments.
DELETE
FROM travel.amendment_attachment
WHERE amendment_id NOT IN (
    SELECT max(amendment_id) as curramd
    FROM travel.amendment
    GROUP BY app_id
);

DELETE
FROM travel.attachment
WHERE attachment_id NOT IN (
    SELECT attachment_id
    FROM travel.amendment_attachment
);

ALTER TABLE travel.attachment
    ADD COLUMN app_id int;

-- Initialize app_id column.
UPDATE travel.attachment
SET app_id = amendment.app_id FROM travel.amendment
         INNER JOIN travel.amendment_attachment USING (amendment_id)
WHERE attachment.attachment_id = amendment_attachment.attachment_id;

DELETE
FROM travel.attachment
WHERE app_id is null;

DROP TABLE IF EXISTS travel.amendment_attachment;

ALTER TABLE travel.attachment
    RENAME TO app_attachment;

ALTER TABLE travel.app_attachment
    ADD CONSTRAINT app_attachment_app_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

ALTER TABLE travel.app_attachment
    ALTER COLUMN app_id SET NOT NULL;

CREATE INDEX ON travel.app_attachment (app_id);


----------------------------
-- ** Refactor Route/Legs **
----------------------------

-- Delete inactive legs
DELETE
FROM travel.amendment_legs
WHERE amendment_id NOT IN (
    SELECT max(amendment_id) as curramd
    FROM travel.amendment
    GROUP BY app_id
);

DELETE
FROM travel.leg
WHERE leg_id NOT IN (
    SELECT leg_id
    FROM travel.amendment_legs
);

ALTER TABLE travel.amendment_legs
    RENAME TO app_route;

ALTER TABLE travel.app_route
    ADD COLUMN app_id int;

UPDATE travel.app_route
SET app_id = amendment.app_id FROM travel.amendment
WHERE app_route.amendment_id = amendment.amendment_id;

DELETE
FROM travel.app_route
WHERE app_id is null;

ALTER TABLE travel.app_route
    ALTER COLUMN app_id SET NOT NULL;

ALTER TABLE travel.app_route
DROP
CONSTRAINT IF EXISTS amendment_legs_amendment_id_fkey;

ALTER TABLE travel.app_route
DROP
COLUMN amendment_id;

ALTER TABLE travel.app_route
    RENAME COLUMN amendment_legs_id to app_route_id;

ALTER SEQUENCE travel.amendment_legs_amendment_leg_id_seq
    RENAME TO app_route_id_seq;

ALTER TABLE travel.app_route
    RENAME CONSTRAINT amendment_legs_leg_id_fkey to app_route_leg_id_fkey;

ALTER TABLE travel.app_route
    ADD CONSTRAINT app_route_app_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

CREATE UNIQUE INDEX ON travel.app_route (app_id, leg_id);

ALTER TABLE travel.app_route
    RENAME COLUMN leg_id to app_route_leg_id;

-----

ALTER TABLE travel.leg
    RENAME TO app_route_leg;

ALTER SEQUENCE travel.leg_leg_id_seq
    RENAME TO app_route_leg_id_seq;

ALTER TABLE travel.app_route_leg
    RENAME COLUMN leg_id to app_route_leg_id;

-- Remove ON DELETE CASCADE from destination FK.
ALTER TABLE travel.app_route_leg
DROP
CONSTRAINT leg_from_destination_id_fkey,
    ADD CONSTRAINT leg_from_destination_id_fkey FOREIGN
        KEY (from_destination_id) REFERENCES travel.destination (destination_id);

-- Remove ON DELETE CASCADE from destination FK.
ALTER TABLE travel.app_route_leg
DROP
CONSTRAINT leg_to_destination_id_fkey,
    ADD CONSTRAINT leg_to_destination_id_fkey FOREIGN
        KEY (from_destination_id) REFERENCES travel.destination (destination_id);


-----------------------------------
-- ** Amendment Lodging Per Diem **
-----------------------------------

-- Delete inactive lodging per diems.
DELETE
FROM travel.amendment_lodging_per_diems
WHERE amendment_id NOT IN (
    SELECT max(amendment_id) as curramd
    FROM travel.amendment
    GROUP BY app_id
);

DELETE
FROM travel.amendment_lodging_per_diem
WHERE amendment_lodging_per_diem_id NOT IN (
    SELECT amendment_lodging_per_diem_id
    FROM travel.amendment_lodging_per_diems
);

ALTER TABLE travel.amendment_lodging_per_diem
    RENAME TO app_lodging_per_diem;

ALTER TABLE travel.app_lodging_per_diem
    ADD COLUMN app_id int;

UPDATE travel.app_lodging_per_diem
SET app_id = amendment.app_id FROM travel.amendment
         JOIN travel.amendment_lodging_per_diems
              USING (amendment_id)
WHERE amendment_lodging_per_diems.amendment_lodging_per_diem_id = app_lodging_per_diem.amendment_lodging_per_diem_id;

DELETE
FROM travel.app_lodging_per_diem
WHERE app_id is null;

ALTER TABLE travel.app_lodging_per_diem
    ALTER COLUMN app_id SET NOT NULL;

ALTER TABLE travel.app_lodging_per_diem
    RENAME COLUMN amendment_lodging_per_diem_id to app_lodging_per_diem_id;

ALTER SEQUENCE travel.amendment_lodging_per_diem_amendment_lodging_per_diem_id_seq
    RENAME TO app_lodging_per_diem_id_seq;

ALTER TABLE travel.amendment_lodging_per_diems
DROP
CONSTRAINT IF EXISTS amendment_lodging_per_diems_per_diem_id_fkey;

ALTER TABLE travel.app_lodging_per_diem
DROP
CONSTRAINT amendment_lodging_per_diem_address_id_fkey,
    ADD CONSTRAINT app_lodging_per_diem_address_id_fkey
        FOREIGN KEY (address_id) REFERENCES travel.address (address_id);

ALTER
INDEX travel.amendment_lodging_per_diem_pkey RENAME TO app_lodging_per_diem_pkey;

CREATE UNIQUE INDEX ON travel.app_lodging_per_diem (app_id, date);

ALTER TABLE travel.app_lodging_per_diem
    ADD CONSTRAINT app_lodging_per_diem_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

-- Store lodging per diem overrides in a separate table
CREATE TABLE travel.app_lodging_per_diem_override
(
    app_lodging_per_diem_override_id serial,
    app_id                           int  NOT NULL,
    override_rate                    text NOT NULL
);

DROP TABLE travel.amendment_lodging_per_diems;

CREATE INDEX ON travel.app_lodging_per_diem_override(app_id);

ALTER TABLE travel.app_lodging_per_diem_override
    ADD CONSTRAINT app_lodging_per_diem_override_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

-----------------------------------
-- ** Amendment Meal Per Diem **
-----------------------------------

DELETE
FROM travel.amendment_meal_per_diems
WHERE amendment_id NOT IN (
    SELECT max(amendment_id) as curramd
    FROM travel.amendment
    GROUP BY app_id
);

DELETE
FROM travel.amendment_meal_per_diem
WHERE amendment_meal_per_diem_id NOT IN (
    SELECT amendment_meal_per_diem_id
    FROM travel.amendment_meal_per_diems
);

ALTER TABLE travel.amendment_meal_per_diem
    RENAME TO app_meal_per_diem;

ALTER TABLE travel.app_meal_per_diem
    ADD COLUMN app_id int;

UPDATE travel.app_meal_per_diem
SET app_id = amendment.app_id FROM travel.amendment
        JOIN travel.amendment_meal_per_diems USING(amendment_id)
WHERE amendment_meal_per_diems.amendment_meal_per_diem_id = app_meal_per_diem.amendment_meal_per_diem_id;

ALTER TABLE travel.amendment_meal_per_diems
DROP
CONSTRAINT IF EXISTS amendment_meal_per_diems_per_diem_id_fkey;

DELETE
FROM travel.app_meal_per_diem
WHERE app_id is null;

ALTER TABLE travel.app_meal_per_diem
    ALTER COLUMN app_id SET NOT NULL;

ALTER TABLE travel.app_meal_per_diem
    RENAME COLUMN amendment_meal_per_diem_id TO app_meal_per_diem_id;

ALTER SEQUENCE travel.amendment_meal_per_diem_amendment_meal_per_diem_id_seq
    RENAME TO app_meal_per_diem_id_seq;

ALTER TABLE travel.app_meal_per_diem
DROP
CONSTRAINT amendment_meal_per_diem_address_id_fkey,
    ADD CONSTRAINT app_meal_per_diem_address_id_fkey
         FOREIGN KEY (address_id) REFERENCES travel.address(address_id),
    DROP
CONSTRAINT amendment_meal_per_diem_senate_mie_id_fkey,
    ADD CONSTRAINT app_meal_per_diem_senate_mie_id_fkey
         FOREIGN KEY (senate_mie_id) REFERENCES travel.senate_mie(senate_mie_id);

ALTER
INDEX travel.amendment_meal_per_diem_pkey RENAME TO app_meal_per_diem_pkey;

CREATE UNIQUE INDEX ON travel.app_meal_per_diem(app_id, date);

ALTER TABLE travel.app_meal_per_diem
    ADD CONSTRAINT app_meal_per_diem_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

-- Store meal per diem overrides in a separate table
CREATE TABLE travel.app_meal_per_diem_override
(
    app_meal_per_diem_override_id serial,
    app_id                        int  NOT NULL,
    override_rate                 text NOT NULL
);

DROP TABLE travel.amendment_meal_per_diems;

CREATE INDEX ON travel.app_meal_per_diem_override(app_id);

ALTER TABLE travel.app_meal_per_diem_override
    ADD CONSTRAINT app_meal_per_diem_override_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

-----------------------------------
-- ** Amendment Mileage Per Diem **
-----------------------------------

-- Delete date from old amendments.
DELETE
FROM travel.amendment_mileage_per_diems
WHERE amendment_id NOT IN (SELECT max(amendment_id) as curramd
                           FROM travel.amendment
                           GROUP BY app_id);

DELETE
FROM travel.amendment_mileage_per_diem
WHERE amendment_mileage_per_diem_id NOT IN (SELECT amendment_mileage_per_diem_id
                                            FROM travel.amendment_mileage_per_diems);

ALTER TABLE travel.amendment_mileage_per_diem
    RENAME TO app_mileage_per_diem;

ALTER TABLE travel.app_mileage_per_diem
    ADD COLUMN app_id int;

UPDATE travel.app_mileage_per_diem
SET app_id = amendment.app_id FROM travel.amendment
        JOIN travel.amendment_mileage_per_diems USING(amendment_id)
WHERE amendment_mileage_per_diems.amendment_mileage_per_diem_id = app_mileage_per_diem.amendment_mileage_per_diem_id;

ALTER TABLE travel.amendment_mileage_per_diems
DROP
CONSTRAINT IF EXISTS amendment_mileage_per_diems_amendment_mileage_per_diem_id_fkey;

DELETE
FROM travel.app_mileage_per_diem
WHERE app_id is null;

ALTER TABLE travel.app_mileage_per_diem
    ALTER COLUMN app_id SET NOT NULL;

ALTER TABLE travel.app_mileage_per_diem
    RENAME COLUMN amendment_mileage_per_diem_id TO app_mileage_per_diem_id;

ALTER SEQUENCE travel.amendment_mileage_per_diem_amendment_mileage_per_diem_id_seq
    RENAME TO app_mileage_per_diem_id_seq;

ALTER TABLE travel.app_mileage_per_diem
DROP
CONSTRAINT amendment_mileage_per_diem_from_address_id_fkey,
     ADD CONSTRAINT app_mileage_per_diem_from_address_id_fkey
        FOREIGN KEY (from_address_id) REFERENCES travel.address(address_id),
DROP
CONSTRAINT amendment_mileage_per_diem_to_address_id_fkey,
     ADD CONSTRAINT app_mileage_per_diem_to_address_id_fkey
        FOREIGN KEY (to_address_id) REFERENCES travel.address(address_id);

ALTER
INDEX travel.amendment_mileage_per_diem_pkey
      RENAME TO app_mileage_per_diem_pkey;

CREATE UNIQUE INDEX ON travel.app_mileage_per_diem(app_id, sequence_no);

ALTER TABLE travel.app_mileage_per_diem
    ADD CONSTRAINT app_mileage_per_diem_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;

-- Store meal per diem overrides in a separate table
CREATE TABLE travel.app_mileage_per_diem_override
(
    app_mileage_per_diem_override_id serial,
    app_id                           int  NOT NULL,
    override_rate                    text NOT NULL
);

DROP TABLE travel.amendment_mileage_per_diems;

CREATE INDEX ON travel.app_mileage_per_diem_override(app_id);

ALTER TABLE travel.app_mileage_per_diem_override
    ADD CONSTRAINT app_mileage_per_diem_override_app_id_fkey FOREIGN KEY (app_id)
        REFERENCES travel.app (app_id)
        ON DELETE CASCADE;


-----------------------------------
-- ** Amendment **
-----------------------------------

DELETE
FROM travel.amendment
WHERE amendment_id NOT IN (
    SELECT max(amendment_id) as curramd
    FROM travel.amendment
    GROUP BY app_id
);

ALTER TABLE travel.app
    ADD COLUMN additional_purpose text,
ADD COLUMN event_type text,
ADD COLUMN event_name text,
ADD COLUMN modified_by int,
ADD COLUMN modified_date_time timestamp with time zone default now();

UPDATE travel.app
SET additional_purpose = amd.additional_purpose,
    event_type = amd.event_type,
    event_name = amd.event_name,
    modified_by = amd.created_by,
    modified_date_time = amd.created_date_time
    FROM (SELECT * FROM travel.amendment) as amd
WHERE app.app_id = amd.app_id;

DROP TABLE travel.amendment;


