
CREATE TABLE ess.emergency_notification_info (
  employee_id INT PRIMARY KEY NOT NULL,
  phone_home TEXT,
  phone_mobile TEXT,
  phone_alternate TEXT,
  sms_subscribed BOOLEAN NOT NULL DEFAULT FALSE,
  email_personal TEXT,
  email_alternate TEXT
);

GRANT ALL PRIVILEGES ON TABLE ess.emergency_notification_info TO PUBLIC;
