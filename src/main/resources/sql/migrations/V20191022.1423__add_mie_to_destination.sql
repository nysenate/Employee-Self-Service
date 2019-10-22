ALTER TABLE travel.destination ADD COLUMN gsa_mie_id int NOT NULL references gsa_mie(gsa_mie_id);

ALTER TABLE travel.destination DELETE COLUMN value;