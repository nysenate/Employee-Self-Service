
ALTER TABLE travel.app_meal_per_diem_override
RENAME TO app_meal_per_diem_adjustments;

ALTER TABLE travel.app_meal_per_diem_adjustments
ADD COLUMN is_allowed_meals bool NOT NULL DEFAULT false;

-- Initialize with default values for every existing app.
INSERT INTO travel.app_meal_per_diem_adjustments(app_id, override_rate, is_allowed_meals)
SELECT app_id, '0.00', true FROM travel.app;
