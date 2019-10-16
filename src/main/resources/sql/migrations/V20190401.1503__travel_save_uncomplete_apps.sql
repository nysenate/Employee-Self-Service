ALTER TABLE travel.app ALTER COLUMN submitted_date_time drop not null;
ALTER TABLE travel.app ALTER COLUMN submitted_date_time drop default;

ALTER TABLE travel.app_version_allowance DROP CONSTRAINT app_version_allowance_app_version_id_fkey;

ALTER TABLE travel.app_version_allowance
    ADD CONSTRAINT app_version_allowance_app_version_id_fkey FOREIGN KEY(app_version_id) REFERENCES travel.app_version(app_version_id) ON DELETE CASCADE;

ALTER TABLE travel.app_version_allowance DROP CONSTRAINT app_version_allowance_allowance_id_fkey;

ALTER TABLE travel.app_version_allowance
    ADD CONSTRAINT app_version_allowance_allowance_id_fkey FOREIGN KEY(allowance_id) REFERENCES travel.app_allowance(allowance_id) ON DELETE CASCADE;

ALTER TABLE travel.app_version DROP CONSTRAINT app_version_app_id_fkey;

ALTER TABLE travel.app_version
    ADD CONSTRAINT app_version_app_id_fkey FOREIGN KEY(app_id) REFERENCES travel.app(app_id) ON DELETE CASCADE;

ALTER TABLE ONLY travel.app_version_leg DROP CONSTRAINT app_version_leg_app_version_id_fkey;

ALTER TABLE ONLY travel.app_version_leg
    ADD CONSTRAINT app_version_leg_app_version_id_fkey FOREIGN KEY(app_version_id) REFERENCES travel.app_version(app_version_id) ON DELETE CASCADE;

ALTER TABLE ONLY travel.app_version_leg DROP CONSTRAINT app_version_leg_leg_id_fkey;

ALTER TABLE ONLY travel.app_version_leg
    ADD CONSTRAINT app_version_leg_leg_id_fkey FOREIGN KEY(leg_id) REFERENCES travel.app_leg(leg_id) ON DELETE CASCADE;

ALTER TABLE ONLY travel.app_leg DROP CONSTRAINT app_leg_from_destination_id_fkey;

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_from_destination_id_fkey FOREIGN KEY (from_destination_id) REFERENCES travel.app_leg_destination(destination_id) ON DELETE CASCADE;

ALTER TABLE ONLY travel.app_leg DROP CONSTRAINT app_leg_to_destination_id_fkey;

ALTER TABLE ONLY travel.app_leg
    ADD CONSTRAINT app_leg_to_destination_id_fkey FOREIGN KEY (to_destination_id) REFERENCES travel.app_leg_destination(destination_id) ON DELETE CASCADE;

ALTER TABLE ONLY travel.app_leg_destination_meal_per_diem DROP CONSTRAINT app_leg_destination_meal_per_diem_destination_id_fkey;

ALTER TABLE ONLY travel.app_leg_destination_meal_per_diem
    ADD CONSTRAINT app_leg_destination_meal_per_diem_destination_id_fkey FOREIGN KEY (destination_id) REFERENCES travel.app_leg_destination(destination_id) ON DELETE CASCADE;

ALTER TABLE ONLY travel.app_leg_destination_lodging_per_diem DROP CONSTRAINT app_leg_destination_loding_per_diem_destination_id_fkey;

ALTER TABLE ONLY travel.app_leg_destination_lodging_per_diem
  ADD CONSTRAINT app_leg_destination_loding_per_diem_destination_id_fkey FOREIGN KEY (destination_id) REFERENCES travel.app_leg_destination(destination_id) ON DELETE CASCADE;

DROP TABLE travel.uncompleted_travel_application;

ALTER TABLE ONLY travel.app_version
  ADD COLUMN submitted_date_time timestamp with time zone;

UPDATE travel.app_version
  SET submitted_date_time = app.submitted_date_time
  FROM travel.app
  WHERE app.app_id = app_version.app_id
  AND app.app_version_id = app_version.app_version_id;

ALTER TABLE travel.app
DROP COLUMN submitted_date_time;

ALTER TABLE travel.app_version
  ALTER COLUMN created_date_time TYPE timestamp with time zone;