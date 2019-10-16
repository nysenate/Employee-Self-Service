CREATE TABLE travel.app_approval(
  app_approval_id SERIAL PRIMARY KEY,
  app_id int NOT NULL,
  traveler_role text,
  next_reviewer_role text
);

CREATE TABLE travel.app_approval_action (
  app_approval_action_id SERIAL PRIMARY KEY,
  app_approval_id int NOT NULL,
  employee_id int NOT NULL,
  role text NOT NULL,
  type text NOT NULL,
  notes text,
  date_time timestamp with time zone DEFAULT now() NOT NULL
);

ALTER TABLE travel.app_approval_action
  ADD CONSTRAINT app_approval_action_app_approval_id_fkey FOREIGN KEY(app_approval_id) REFERENCES travel.app_approval(app_approval_id);

ALTER TABLE travel.app_approval
  ADD CONSTRAINT  app_approval_app_id_fkey FOREIGN KEY(app_id) REFERENCES travel.app(app_id);
