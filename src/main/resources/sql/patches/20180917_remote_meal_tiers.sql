ALTER TABLE travel.app_meal_allowance
    DROP CONSTRAINT app_meal_allowance_meal_tier_id_fkey;

ALTER TABLE travel.app_meal_allowance
    DROP COLUMN meal_tier_id;

ALTER TABLE travel.app_meal_allowance
    ADD COLUMN meal_rate text;

DROP TABLE travel.meal_tier;
DROP TABLE travel.meal_rate;