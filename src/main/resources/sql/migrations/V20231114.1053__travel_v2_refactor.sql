
-- Update constraints
ALTER TABLE travel.amendment_meal_per_diems
DROP CONSTRAINT amendment_meal_per_diems_amendment_id_fkey;

ALTER TABLE travel.amendment_meal_per_diems
    ADD CONSTRAINT amendment_meal_per_diems_amendment_id_fkey FOREIGN KEY (amendment_id)
        REFERENCES travel.amendment(amendment_id) ON DELETE CASCADE;

ALTER TABLE travel.amendment_lodging_per_diems
DROP CONSTRAINT amendment_lodging_per_diems_amendment_id_fkey;

ALTER TABLE travel.amendment_lodging_per_diems
    ADD CONSTRAINT amendment_lodging_per_diems_amendment_id_fkey FOREIGN KEY (amendment_id)
        REFERENCES travel.amendment(amendment_id) ON DELETE CASCADE;

ALTER TABLE travel.amendment_lodging_per_diems
DROP CONSTRAINT amendment_lodging_per_diems_per_diem_id_fkey;

ALTER TABLE travel.amendment_lodging_per_diems
    ADD CONSTRAINT amendment_lodging_per_diems_per_diem_id_fkey FOREIGN KEY (amendment_lodging_per_diem_id)
        REFERENCES travel.amendment_lodging_per_diem(amendment_lodging_per_diem_id) ON DELETE CASCADE;

ALTER TABLE travel.amendment_mileage_per_diems
DROP CONSTRAINT amendment_mileage_per_diems_amendment_id_fkey;

ALTER TABLE travel.amendment_mileage_per_diems
    ADD CONSTRAINT amendment_mileage_per_diems_amendment_id_fkey FOREIGN KEY (amendment_id)
        REFERENCES travel.amendment(amendment_id) ON DELETE CASCADE;

ALTER TABLE travel.amendment_meal_per_diems
DROP CONSTRAINT amendment_meal_per_diems_per_diem_id_fkey;

ALTER TABLE travel.amendment_meal_per_diems
    ADD CONSTRAINT amendment_meal_per_diems_per_diem_id_fkey FOREIGN KEY (amendment_meal_per_diem_id)
        REFERENCES travel.amendment_meal_per_diem(amendment_meal_per_diem_id) ON DELETE CASCADE;

ALTER TABLE travel.amendment_attachment
DROP CONSTRAINT amendment_attachment_amendment_id_fkey;

ALTER TABLE travel.amendment_attachment
    ADD CONSTRAINT amendment_attachment_amendment_id_fkey FOREIGN KEY (amendment_id)
        REFERENCES travel.amendment(amendment_id) ON DELETE CASCADE;

ALTER TABLE travel.amendment_mileage_per_diems
DROP CONSTRAINT amendment_mileage_per_diems_amendment_mileage_per_diem_id_fkey;

ALTER TABLE travel.amendment_mileage_per_diems
    ADD CONSTRAINT amendment_mileage_per_diems_amendment_mileage_per_diem_id_fkey FOREIGN KEY (amendment_mileage_per_diem_id)
        REFERENCES travel.amendment_mileage_per_diem(amendment_mileage_per_diem_id) ON DELETE CASCADE;


-- Temp save old amendment versions to be deleted.
CREATE TEMP TABLE old_amd_vers(
    amd_id INT
);

WITH curr_amd_versions as (
    SELECT DISTINCT ON (app_id) amendment_id
FROM travel.amendment
ORDER BY app_id, amendment_id DESC
    ), old_amd_versions AS (
SELECT amendment.amendment_id
FROM travel.amendment
WHERE amendment.amendment_id NOT IN (select * from curr_amd_versions)
    )
INSERT INTO old_amd_vers
SELECT * FROM old_amd_versions;

-- Delete old legs
DELETE FROM travel.leg
WHERE EXISTS (
    SELECT leg_id
    FROM travel.amendment_legs
             JOIN old_amd_vers ON amendment_legs.amendment_id = old_amd_vers.amd_id
    WHERE leg.leg_id = amendment_legs.leg_id
);

-- Delete old Lodging Per Diems
DELETE FROM travel.amendment_lodging_per_diem
WHERE EXISTS (
    SELECT *
    FROM travel.amendment_lodging_per_diems
             JOIN old_amd_vers ON amendment_lodging_per_diems.amendment_id = old_amd_vers.amd_id
    WHERE amendment_lodging_per_diem.amendment_lodging_per_diem_id = amendment_lodging_per_diems.amendment_lodging_per_diem_id
);

-- Delete old meal per diems
DELETE FROM travel.amendment_meal_per_diem
WHERE EXISTS (
    SELECT *
    FROM travel.amendment_meal_per_diems
             JOIN old_amd_vers ON amendment_meal_per_diems.amendment_id = old_amd_vers.amd_id
    WHERE amendment_meal_per_diem.amendment_meal_per_diem_id = amendment_meal_per_diems.amendment_meal_per_diem_id
);

-- Delete old mileage per diems
DELETE FROM travel.amendment_mileage_per_diem
WHERE EXISTS (
    SELECT *
    FROM travel.amendment_mileage_per_diems
             JOIN old_amd_vers ON amendment_mileage_per_diems.amendment_id = old_amd_vers.amd_id
    WHERE amendment_mileage_per_diem.amendment_mileage_per_diem_id = amendment_mileage_per_diems.amendment_mileage_per_diem_id
);

-- Delete old amd versions
DELETE FROM travel.amendment
WHERE EXISTS (
    SELECT *
    FROM old_amd_vers
    WHERE amendment.amendment_id = old_amd_vers.amd_id
);

ALTER TABLE travel.amendment
DROP COLUMN version;
