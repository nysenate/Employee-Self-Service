CREATE TABLE travel.attachment (
    attachment_id uuid NOT NULL PRIMARY KEY,
    original_filename text,
    content_type text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);

CREATE TABLE travel.amendment_attachment (
    amendment_id int NOT NULL,
    attachment_id uuid NOT NULL,
    PRIMARY KEY (amendment_id, attachment_id)
);

ALTER TABLE travel.amendment_attachment
    ADD CONSTRAINT amendment_attachment_amendment_id_fkey FOREIGN KEY (amendment_id)
    REFERENCES travel.amendment(amendment_id);

ALTER TABLE travel.amendment_attachment
    ADD CONSTRAINT amendment_attachment_attachment_id_fkey FOREIGN KEY (attachment_id)
    REFERENCES travel.attachment(attachment_id);
