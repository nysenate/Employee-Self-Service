-- This Refactors the delegate table to delegation

ALTER TABLE travel.delegate RENAME TO delegation;

ALTER TABLE travel.delegation
RENAME COLUMN delegate_id to delegation_id;

ALTER SEQUENCE travel.delegate_delegate_id_seq RENAME TO delegation_delegation_id_seq;

ALTER INDEX travel.delegate_pkey RENAME TO delegation_pkey;