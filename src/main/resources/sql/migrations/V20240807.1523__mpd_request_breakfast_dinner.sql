-- Update the meal per diem table to allow requesting of breakfast and dinner separately

-- Add is_breakfast_requested column.
ALTER TABLE travel.app_meal_per_diem
    ADD COLUMN IF NOT EXISTS is_breakfast_requested boolean NOT NULL DEFAULT true;

-- Add is_dinner_requested column.
ALTER TABLE travel.app_meal_per_diem
    ADD COLUMN IF NOT EXISTS is_dinner_requested boolean NOT NULL DEFAULT true;

-- Initialize values
UPDATE travel.app_meal_per_diem
    SET is_breakfast_requested = is_reimbursement_requested,
    is_dinner_requested = is_reimbursement_requested;

-- Remove is_reimbursement_requested column.
ALTER TABLE travel.app_meal_per_diem
DROP COLUMN is_reimbursement_requested;
