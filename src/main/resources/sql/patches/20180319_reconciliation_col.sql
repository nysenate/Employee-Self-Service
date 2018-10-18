ALTER TABLE supply.requisition_content
  ADD COLUMN is_reconciled boolean NOT NULL DEFAULT FALSE;

UPDATE supply.requisition_content rc
SET is_reconciled = true
WHERE rc.revision_id IN
  (SELECT r.current_revision_id FROM supply.requisition r INNER JOIN supply.requisition_content rc
    ON r.requisition_id = rc.requisition_id AND r.current_revision_id = rc.revision_id
    WHERE rc.STATUS = 'APPROVED')