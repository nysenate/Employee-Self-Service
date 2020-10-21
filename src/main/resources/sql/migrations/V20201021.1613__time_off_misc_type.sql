--
-- Re: Time Off Requests
-- Modifying V20190723.1708__time_off_request.sql
-- Rename the time off misc type to PARENTAL_LEAVE from "PARENTAL LEAVE"
--

SET search_path = time, pg_catalog;
ALTER TYPE time_off_misc_type RENAME VALUE 'PARENTAL LEAVE' TO 'PARENTAL_LEAVE';
