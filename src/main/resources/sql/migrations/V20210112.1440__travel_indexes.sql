CREATE INDEX app_traveler_department_id_index
  ON travel.app (traveler_department_id);

CREATE INDEX app_review_next_reviewer_role_index
  ON travel.app_review (next_reviewer_role);

CREATE INDEX app_review_action_app_review_id
  ON travel.app_review_action (app_review_id);

CREATE INDEX app_review_action_role
  ON travel.app_review_action (role);