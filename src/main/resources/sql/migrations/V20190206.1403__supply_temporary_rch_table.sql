CREATE TABLE IF NOT EXISTS supply.employee_temporary_rch (
    id                  integer                          not null,
    employee_id         smallint                         not null,
    rch_code            text                             not null,
    start_date          timestamp without TIME ZONE      not null   default now(),
    end_date            timestamp without TIME ZONE      not null   default '2999-12-31'::timestamp without TIME ZONE
);

COMMENT ON TABLE supply.employee_temporary_rch IS 'Identifies temporary responsibility center heads an employee can order for in addition to their own.';
COMMENT ON COLUMN supply.employee_temporary_rch.rch_code IS 'The code representing a responsibility center head.';
COMMENT ON COLUMN supply.employee_temporary_rch.start_date IS 'The effective start date this record is valid for, inclusive.';
COMMENT ON COLUMN supply.employee_temporary_rch.end_date IS 'The effective end date this record is valid for, inclusive.';

CREATE SEQUENCE supply.employee_temporary_rch_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER SEQUENCE supply.employee_temporary_rch_id_seq OWNED BY supply.employee_temporary_rch.id;

ALTER TABLE ONLY supply.employee_temporary_rch ALTER COLUMN id SET DEFAULT nextval('supply.employee_temporary_rch_id_seq'::regclass);

ALTER TABLE ONLY supply.employee_temporary_rch
    ADD CONSTRAINT employee_temporary_rch_pkey PRIMARY KEY (id);

ALTER TABLE ONLY supply.employee_temporary_rch
    ADD CONSTRAINT employee_temporary_rch_unique UNIQUE (employee_id, rch_code, start_date, end_date);


