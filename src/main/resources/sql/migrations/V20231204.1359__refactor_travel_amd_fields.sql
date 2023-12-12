----------------------------
-- ** Refactor Allowances **
----------------------------

-- Add FK to app_id
ALTER TABLE travel.allowance
    ADD COLUMN app_id int;

-- Initialize app_id column.
UPDATE travel.allowance
SET app_id = amendment.app_id
    FROM travel.amendment
    INNER JOIN travel.amendment_allowances USING (amendment_id)
WHERE allowance.allowance_id = amendment_allowances.allowance_id;

-- Delete old records for non current amendment versions
DELETE FROM travel.allowance
WHERE app_id is null;


ALTER TABLE travel.amendment_allowances
DROP CONSTRAINT IF EXISTS amendment_allowances_amendment_allowances_id_fkey;

DROP TABLE IF EXISTS travel.amendment_allowances;

ALTER TABLE travel.allowance
    RENAME TO app_allowance;

ALTER TABLE travel.app_allowance
    ADD CONSTRAINT app_allowance_app_app_id_fkey FOREIGN KEY(app_id)
        REFERENCES travel.app(app_id);

ALTER TABLE travel.app_allowance
    ALTER COLUMN app_id SET NOT NULL;

CREATE UNIQUE INDEX ON travel.app_allowance(app_id, type);


----------------------------
-- ** Refactor Attachments **
----------------------------

ALTER TABLE travel.attachment
    ADD COLUMN app_id int;

-- Initialize app_id column.
UPDATE travel.attachment
SET app_id = amendment.app_id
    FROM travel.amendment
    INNER JOIN travel.amendment_attachment USING (amendment_id)
WHERE attachment.attachment_id = amendment_attachment.attachment_id;

DELETE FROM travel.attachment
WHERE app_id is null;

DROP TABLE IF EXISTS travel.amendment_attachment;

ALTER TABLE travel.attachment
    RENAME TO app_attachment;

ALTER TABLE travel.app_attachment
    ADD CONSTRAINT app_attachment_app_app_id_fkey FOREIGN KEY(app_id)
        REFERENCES travel.app(app_id)
        ON DELETE CASCADE;

ALTER TABLE travel.app_attachment
    ALTER COLUMN app_id SET NOT NULL;

CREATE INDEX ON travel.app_attachment(app_id);




