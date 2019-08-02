-- Restructuring to store changes to a travel application in Amendments instead of a single active version.

ALTER TABLE travel.app_version RENAME TO amendment;
ALTER TABLE travel.amendment RENAME COLUMN app_version_id TO amendment_id;
ALTER INDEX travel.app_version_pkey RENAME TO amendment_id_pkey;
ALTER TABLE travel.amendment RENAME CONSTRAINT app_version_app_id_fkey TO amendment_app_id_fkey;
ALTER SEQUENCE travel.app_version_app_version_id_seq RENAME TO amendment_amendment_id_seq;
ALTER TABLE travel.amendment DROP COLUMN submitted_date_time;

ALTER TABLE travel.amendment ADD COLUMN version text;

-- Set current version as amendment A and delete older versions.
-- The older versions are really just in progress saves of the current version. We don't need them.
UPDATE travel.amendment SET version = 'A'
WHERE amendment.amendment_id IN
(select amendment.amendment_id from travel.amendment
INNER JOIN travel.app ON app.app_version_id = amendment.amendment_id);

DELETE FROM travel.amendment WHERE version IS NULL;

ALTER TABLE travel.amendment ALTER version SET NOT NULL;
ALTER TABLE travel.amendment ADD CONSTRAINT amendment_app_id_version_unique UNIQUE(app_id, version);

ALTER TABLE travel.app DROP COLUMN app_version_id;

ALTER TABLE travel.app_allowance RENAME TO allowance;
ALTER INDEX travel.app_allowance_pkey RENAME TO allowance_pkey;
ALTER SEQUENCE travel.app_allowance_allowance_id_seq RENAME TO allowance_allowance_id_seq;
ALTER TABLE travel.allowance RENAME COLUMN allowance_type TO type;
ALTER TABLE travel.allowance RENAME COLUMN allowance TO value;

ALTER TABLE travel.app_version_allowance RENAME TO amendment_allowances;
ALTER INDEX travel.app_version_allowance_pkey RENAME TO amendment_allowances_pkey;
ALTER SEQUENCE travel.app_version_allowance_app_version_allowance_id_seq RENAME TO amendment_allowances_amendment_allowances_id_seq;
ALTER TABLE travel.amendment_allowances RENAME COLUMN app_version_allowance_id TO amendment_allowances_id;
ALTER TABLE travel.amendment_allowances RENAME COLUMN app_version_id TO amendment_id;
ALTER TABLE travel.amendment_allowances RENAME CONSTRAINT app_version_allowance_allowance_id_fkey TO amendment_allowances_amendment_allowances_id_fkey;
ALTER TABLE travel.amendment_allowances RENAME CONSTRAINT app_version_allowance_app_version_id_fkey TO amendment_allowances_amendment_id_fkey;

ALTER TABLE travel.app_perdiem_override RENAME TO perdiem_override;
ALTER INDEX travel.app_perdiem_override_pkey RENAME TO perdiem_override_pkey;
ALTER SEQUENCE travel.app_perdiem_override_app_perdiem_override_id_seq RENAME TO perdiem_override_perdiem_override_id_seq;
ALTER TABLE travel.perdiem_override RENAME COLUMN app_perdiem_override_id TO perdiem_override_id;
ALTER TABLE travel.perdiem_override RENAME COLUMN perdiem_type TO type;
ALTER TABLE travel.perdiem_override RENAME COLUMN dollars TO value;

ALTER TABLE travel.app_version_perdiem_override RENAME TO amendment_perdiem_overrides;
ALTER INDEX travel.app_version_perdiem_override_pkey RENAME TO amendment_perdiem_overrides_pkey;
ALTER SEQUENCE travel.app_version_perdiem_override_app_version_perdiem_override_i_seq RENAME TO amendment_perdiem_overrides_amendment_perdiem_overrides_id_seq;
ALTER TABLE travel.amendment_perdiem_overrides RENAME COLUMN app_version_perdiem_override_id TO amendment_perdiem_overrides_id;
ALTER TABLE travel.amendment_perdiem_overrides RENAME COLUMN app_version_id TO amendment_id;
ALTER TABLE travel.amendment_perdiem_overrides RENAME COLUMN app_perdiem_override_id TO perdiem_override_id;
ALTER TABLE travel.amendment_perdiem_overrides RENAME CONSTRAINT app_version_perdiem_override_app_perdiem_override_id_fkey TO amendment_perdiem_overrides_perdiem_override_id_fkey;
ALTER TABLE travel.amendment_perdiem_overrides RENAME CONSTRAINT app_version_perdiem_override_app_version_id_fkey TO amendment_perdiem_overrides_amendment_id_fkey;

ALTER TABLE travel.app_version_status RENAME TO amendment_status;
ALTER INDEX travel.app_version_status_pkey RENAME TO amendment_status_pkey;
ALTER SEQUENCE travel.app_version_status_app_version_status_id_seq RENAME TO amendment_status_amendment_status_id_seq;
ALTER TABLE travel.amendment_status RENAME COLUMN app_version_status_id TO amendment_status_id;
ALTER TABLE travel.amendment_status RENAME COLUMN app_version_id TO amendment_id;
ALTER TABLE travel.amendment_status RENAME CONSTRAINT app_version_status_app_version_id_fkey TO amendment_status_amendment_id_fkey;

ALTER TABLE travel.app_leg RENAME TO leg;
ALTER INDEX travel.app_leg_pkey RENAME TO leg_pkey;
ALTER SEQUENCE travel.app_leg_leg_id_seq RENAME TO leg_leg_id_seq;
ALTER TABLE travel.leg RENAME CONSTRAINT app_leg_from_destination_id_fkey TO leg_from_destination_id_fkey;
ALTER TABLE travel.leg RENAME CONSTRAINT app_leg_to_destination_id_fkey TO leg_to_destination_id_fkey;

ALTER TABLE travel.app_version_leg RENAME TO amendment_legs;
ALTER INDEX travel.app_version_leg_pkey RENAME TO amendment_legs_pkey;
ALTER SEQUENCE travel.app_version_leg_app_version_leg_id_seq RENAME TO amendment_legs_amendment_leg_id_seq;
ALTER TABLE travel.amendment_legs RENAME COLUMN app_version_leg_id TO amendment_legs_id;
ALTER TABLE travel.amendment_legs RENAME COLUMN app_version_id TO amendment_id;
ALTER TABLE travel.amendment_legs RENAME CONSTRAINT app_version_leg_app_version_id_fkey TO amendment_legs_amendment_id_fkey;
ALTER TABLE travel.amendment_legs RENAME CONSTRAINT app_version_leg_leg_id_fkey TO amendment_legs_leg_id_fkey;

ALTER TABLE travel.app_leg_destination RENAME TO destination;
ALTER INDEX travel.app_leg_destination_pkey RENAME TO destination_pkey;
ALTER SEQUENCE travel.app_leg_destination_destination_id_seq RENAME TO destination_destination_id_seq;

ALTER TABLE travel.app_leg_destination_lodging_per_diem RENAME TO destination_lodging_perdiem;
ALTER TABLE travel.destination_lodging_perdiem RENAME COLUMN dollars TO value;
ALTER TABLE travel.destination_lodging_perdiem RENAME CONSTRAINT app_leg_destination_loding_per_diem_destination_id_fkey TO destination_lodging_perdiem_destination_id_fkey;
ALTER TABLE travel.destination_lodging_perdiem ADD PRIMARY KEY (destination_id, date);

ALTER TABLE travel.app_leg_destination_meal_per_diem RENAME TO destination_meal_perdiem;
ALTER TABLE travel.destination_meal_perdiem RENAME COLUMN dollars TO value;
ALTER TABLE travel.destination_meal_perdiem RENAME CONSTRAINT app_leg_destination_meal_per_diem_destination_id_fkey TO destination_meal_perdiem_destination_id_fkey;
ALTER TABLE travel.destination_meal_perdiem ADD PRIMARY KEY (destination_id, date);
