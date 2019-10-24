ALTER TABLE travel.app_leg_destination_meal_per_diem
  RENAME per_diem TO dollars;

ALTER TABLE travel.app_leg_destination_lodging_per_diem
  RENAME per_diem TO dollars;

ALTER TABLE travel.app_leg_destination_meal_per_diem
  ADD COLUMN is_reimbursement_requested boolean NOT NULL DEFAULT true;

ALTER TABLE travel.app_leg_destination_lodging_per_diem
  ADD COLUMN is_reimbursement_requested boolean NOT NULL DEFAULT true;

ALTER TABLE travel.app_leg
  ADD COLUMN is_reimbursement_requested boolean NOT NULL DEFAULT true;
