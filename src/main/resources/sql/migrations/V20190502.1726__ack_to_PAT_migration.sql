
SET SEARCH_PATH = ess;

--- Add existing acknowledgments as personnel assigned tasks

--- First add the standard acks: completed by employees, for themselves, using the interface.
INSERT INTO personnel_assigned_task
    (emp_id, task_type, task_number, timestamp, update_user_id, completed)
SELECT emp_id, 'DOCUMENT_ACKNOWLEDGMENT'::personnel_task_type, ack_doc_id, timestamp, emp_id, true
FROM acknowledgment
WHERE personnel_acked = FALSE
;

--- Then add the override acks performed by personnel on behalf of an employee
--- Use -1 for update_user_id since we do not know who made the change based on the ack data model.
INSERT INTO personnel_assigned_task
(emp_id, task_type, task_number, timestamp, update_user_id, completed)
SELECT emp_id, 'DOCUMENT_ACKNOWLEDGMENT'::personnel_task_type, ack_doc_id, timestamp, -1, true
FROM acknowledgment
WHERE personnel_acked = TRUE
;
