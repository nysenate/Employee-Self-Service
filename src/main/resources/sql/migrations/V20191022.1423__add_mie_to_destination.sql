ALTER TABLE travel.destination_meal_per_diem ADD COLUMN gsa_mie_id int NOT NULL references travel.gsa_mie(gsa_mie_id);
ALTER TABLE travel.destination_meal_per_diem DROP COLUMN value;

-- Also add mie to amendment_meal_per_diem table
ALTER TABLE travel.amendment_meal_per_diem ADD COLUMN gsa_mie_id int references travel.gsa_mie(gsa_mie_id);

UPDATE travel.amendment_meal_per_diem SET gsa_mie_id = subquery.gsa_mie_id
FROM (SELECT gsa_mie_id, total FROM travel.gsa_mie) subquery
WHERE rate = subquery.total;

ALTER TABLE travel.amendment_meal_per_diem ALTER COLUMN gsa_mie_id SET NOT NULL;

ALTER TABLE travel.amendment_meal_per_diem DROP COLUMN rate;
