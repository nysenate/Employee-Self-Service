
ALTER TABLE travel.destination_meal_perdiem DROP COLUMN is_reimbursement_requested;
ALTER TABLE travel.destination_lodging_perdiem DROP COLUMN is_reimbursement_requested;

ALTER TABLE travel.destination_meal_perdiem RENAME TO destination_meal_per_diem;
ALTER TABLE travel.destination_lodging_perdiem RENAME TO destination_lodging_per_diem;

-- Meals

CREATE TABLE travel.amendment_meal_per_diems(
    amendment_meal_per_diems_id SERIAL PRIMARY KEY,
    amendment_id int NOT NULL,
    amendment_meal_per_diem_id int NOT NULL,
    override_rate text NOT NULL
);

CREATE TABLE travel.amendment_meal_per_diem(
    amendment_meal_per_diem_id SERIAL PRIMARY KEY,
    google_address_id int NOT NULL,
    date date NOT NULL,
    rate text NOT NULL,
    is_reimbursement_requested boolean NOT NULL
);

ALTER TABLE travel.amendment_meal_per_diems
    ADD CONSTRAINT amendment_meal_per_diems_amendment_id_fkey FOREIGN KEY (amendment_id)
    REFERENCES travel.amendment(amendment_id);

ALTER TABLE travel.amendment_meal_per_diems
    ADD CONSTRAINT amendment_meal_per_diems_per_diem_id_fkey FOREIGN KEY (amendment_meal_per_diem_id)
    REFERENCES travel.amendment_meal_per_diem(amendment_meal_per_diem_id);

ALTER TABLE travel.amendment_meal_per_diem
    ADD CONSTRAINT amendment_meal_per_diem_google_address_id_fkey FOREIGN KEY (google_address_id)
    REFERENCES travel.google_address(google_address_id);

-- Lodging

CREATE TABLE travel.amendment_lodging_per_diems(
    amendment_lodging_per_diems_id SERIAL PRIMARY KEY,
    amendment_id int NOT NULL,
    amendment_lodging_per_diem_id int NOT NULL,
    override_rate text NOT NULL
);

CREATE TABLE travel.amendment_lodging_per_diem(
    amendment_lodging_per_diem_id SERIAL PRIMARY KEY,
    google_address_id int NOT NULL,
    date date NOT NULL,
    rate text NOT NULL,
    is_reimbursement_requested boolean NOT NULL
);

ALTER TABLE travel.amendment_lodging_per_diems
    ADD CONSTRAINT amendment_lodging_per_diems_amendment_id_fkey FOREIGN KEY (amendment_id)
    REFERENCES travel.amendment(amendment_id);

ALTER TABLE travel.amendment_lodging_per_diems
    ADD CONSTRAINT amendment_lodging_per_diems_per_diem_id_fkey FOREIGN KEY (amendment_lodging_per_diem_id)
    REFERENCES travel.amendment_lodging_per_diem(amendment_lodging_per_diem_id);

ALTER TABLE travel.amendment_lodging_per_diem
    ADD CONSTRAINT amendment_lodging_per_diem_google_address_id_fkey FOREIGN KEY (google_address_id)
    REFERENCES travel.google_address(google_address_id);

DROP TABLE IF EXISTS travel.amendment_perdiem_overrides;
DROP TABLE IF EXISTS travel.perdiem_override;
DROP TABLE IF EXISTS travel.travel_requestors;
