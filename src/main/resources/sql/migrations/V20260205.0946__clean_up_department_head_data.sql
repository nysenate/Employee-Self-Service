-- Use 'infinity' to indicate an ongoing date range instead of a null upper bound.
UPDATE ess.department_head
SET effective_date_range = daterange(lower(effective_date_range), 'infinity'::date, '[)')
WHERE upper(effective_date_range) IS NULL;

-- Enforce infinity instead of null in upper bound.
ALTER TABLE ess.department_head
ADD CONSTRAINT check_upper_date_range_not_null
  CHECK (upper(effective_date_range) IS NOT NULL);

-- Enforce lower inclusive, upper exclusive [) effective date range.
ALTER TABLE ess.department_head
ADD CONSTRAINT check_inclusive_exclusive_date_range
  CHECK (lower_inc(effective_date_range) AND NOT upper_inc(effective_date_range));