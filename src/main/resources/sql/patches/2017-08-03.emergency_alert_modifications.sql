
CREATE TYPE ess.mobile_contact_options
  AS ENUM ('CALLS_ONLY', 'TEXTS_ONLY', 'EVERYTHING');


ALTER TABLE ess.emergency_notification_info DROP COLUMN IF EXISTS sms_subscribed;

ALTER TABLE ess.emergency_notification_info
  ADD COLUMN mobile_options ess.mobile_contact_options
    NOT NULL DEFAULT 'CALLS_ONLY'::ess.mobile_contact_options;

ALTER TABLE ess.emergency_notification_info RENAME TO alert_info;
