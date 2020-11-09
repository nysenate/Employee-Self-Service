ALTER TYPE mobile_contact_options RENAME TO contact_options;

ALTER TABLE ess.alert_info ADD alternate_options ess.contact_options;