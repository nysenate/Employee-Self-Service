CREATE TABLE travel.amendment_mileage_per_diem
(
    amendment_mileage_per_diem_id SERIAL PRIMARY KEY,
    from_address_id               int      NOT NULL REFERENCES travel.address (address_id),
    to_address_id                 int      NOT NULL REFERENCES travel.address (address_id),
    travel_date                   date     NOT NULL,
    method_of_travel              text     NOT NULL,
    method_of_travel_description  text     NOT NULL DEFAULT '',
    miles                         text     NOT NULL,
    mileage_rate                  text     NOT NULL,
    is_outbound                   boolean  NOT NULL,
    is_reimbursement_requested    boolean  NOT NULL DEFAULT true,
    sequence_no                   smallint NOT NULL
);

CREATE TABLE travel.amendment_mileage_per_diems
(
    amendment_mileage_per_diems_id SERIAL PRIMARY KEY,
    amendment_id                   int  NOT NULL REFERENCES travel.amendment (amendment_id),
    amendment_mileage_per_diem_id  int  NOT NULL REFERENCES travel.amendment_mileage_per_diem (amendment_mileage_per_diem_id),
    override_rate                  text NOT NULL
);

ALTER TABLE travel.leg
    DROP COLUMN miles,
    DROP COLUMN mileage_rate,
    DROP COLUMN is_reimbursement_requested;