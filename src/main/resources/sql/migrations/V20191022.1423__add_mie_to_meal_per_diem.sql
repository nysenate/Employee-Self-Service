-- add mie to amendment_meal_per_diem table
ALTER TABLE travel.amendment_meal_per_diem ADD COLUMN senate_mie_id int references travel.senate_mie(senate_mie_id);

UPDATE travel.amendment_meal_per_diem SET senate_mie_id = subquery.senate_mie_id
FROM (SELECT senate_mie_id, total FROM travel.senate_mie) subquery
WHERE rate = subquery.total;
