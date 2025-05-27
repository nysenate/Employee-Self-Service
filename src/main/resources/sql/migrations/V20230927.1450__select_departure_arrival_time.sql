ALTER TABLE travel.amendment_meal_per_diem
    ADD COLUMN qualifies_for_breakfast boolean NOT NULL DEFAULT true;

ALTER TABLE travel.amendment_meal_per_diem
    ADD COLUMN qualifies_for_dinner boolean NOT NULL DEFAULT true;

ALTER TABLE travel.amendment_legs
    ADD COLUMN first_leg_qualifies_for_breakfast boolean NOT NULL DEFAULT true;

ALTER TABLE travel.amendment_legs
    ADD COLUMN last_leg_qualifies_for_dinner boolean NOT NULL DEFAULT true;
