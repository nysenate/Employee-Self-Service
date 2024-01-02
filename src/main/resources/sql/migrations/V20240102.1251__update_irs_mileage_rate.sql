UPDATE travel.irs_mileage_rate
SET end_date = '2023-12-31'
WHERE start_date = '2018-01-01';

INSERT INTO travel.irs_mileage_rate(start_date, end_date, rate)
VALUES('2024-01-01', '2999-12-31', '0.67')
ON CONFLICT DO NOTHING;
