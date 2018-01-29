ALTER TABLE ess.ack_doc ALTER COLUMN title set data type text;
ALTER TABLE ess.ack_doc alter COLUMN filename set data type text;
alter TABLE ess.ack_doc alter COLUMN effective_date_time
    set data type timestamp without TIME ZONE USING effective_date_time::timestamp without time zone;

alter table ess.acknowledgement ALTER COLUMN timestamp set data type TIMESTAMP WITHOUT TIME ZONE
    USING timestamp::timestamp without time zone;
