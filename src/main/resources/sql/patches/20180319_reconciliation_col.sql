ALTER TABLE supply.requisition_content
  ADD COLUMN is_reconciled boolean NOT NULL DEFAULT FALSE;
