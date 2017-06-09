
ALTER TABLE supply.requisition_content
ADD COLUMN delivery_method text;

UPDATE supply.requisition_content
SET delivery_method = 'DELIVERY';

ALTER TABLE supply.requisition_content
ALTER COLUMN delivery_method SET NOT NULL;

