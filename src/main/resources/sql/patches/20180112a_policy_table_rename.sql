
ALTER TABLE ess.acknowledgement DROP CONSTRAINT acknowledgement_policy_id_fkey;

ALTER TABLE ess.policy DROP CONSTRAINT policy_pkey;
ALTER TABLE ess.policy RENAME policy_id TO id;
ALTER TABLE ess.policy RENAME TO ack_doc;
ALTER TABLE ess.ack_doc ADD PRIMARY KEY (id);

ALTER TABLE ess.acknowledgement DROP CONSTRAINT acknowledgement_pkey;
ALTER TABLE ess.acknowledgement RENAME policy_id TO ack_doc_id;
ALTER TABLE ess.acknowledgement ADD PRIMARY KEY (emp_id, ack_doc_id);
ALTER TABLE ess.acknowledgement ADD FOREIGN KEY (ack_doc_id) REFERENCES ess.ack_doc (id);
