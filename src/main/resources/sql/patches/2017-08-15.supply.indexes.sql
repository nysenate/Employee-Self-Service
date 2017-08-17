
CREATE INDEX line_item_item_id_cast_text_index
  ON supply.line_item (CAST(item_id as text));

CREATE INDEX requisition_content_destination_index
  ON supply.requisition_content (destination);

CREATE INDEX requisition_content_customer_id_cast_text_index
  ON supply.requisition_content (COALESCE(customer_id::text, ''));

CREATE INDEX requisition_content_issuing_emp_cast_text_index
  ON supply.requisition_content (COALESCE(issuing_emp_id::text, ''));

CREATE INDEX requisition_saved_in_sfms_cast_text_index
  ON supply.requisition (CAST(saved_in_sfms as text));

CREATE INDEX requisition_ordered_date_time_index
  ON supply.requisition (ordered_date_time);

CREATE INDEX requisition_approved_date_time_index
  ON supply.requisition (approved_date_time);

