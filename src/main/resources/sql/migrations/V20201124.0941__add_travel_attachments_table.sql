CREATE TABLE travel.amendment_attachment (
    amendment_attachment_id SERIAL PRIMARY KEY,
    amendment_id INT NOT NULL,
    filename text NOT NULL,
    original_filename text,
    content_type text
);

ALTER TABLE travel.amendment_attachment
    ADD CONSTRAINT amendment_attachment_amendment_id_fkey FOREIGN KEY (amendment_id)
    REFERENCES travel.amendment(amendment_id);
