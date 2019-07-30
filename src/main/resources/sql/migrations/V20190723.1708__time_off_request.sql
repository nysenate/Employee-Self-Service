--
-- Employee Time-Off Requests
--

CREATE SCHEMA IF NOT EXISTS time;
SET search_path = time, pg_catalog;

-- Create types if they don't already exist
DO $$
BEGIN
        --
        -- Types of Miscellaneous time off
        --    Each type is exactly as it is spelled on the
        --    webpage, without punctuation
        --
        CREATE TYPE time_off_misc_type AS ENUM (
            'NO_MISC_HOURS',
            'BEREAVEMENT_LEAVE',
            'BRST_PROST_CANCR_SCRNING',
            'BLOOD_DONATION',
            'EXTRAORDINARY_LEAVE',
            'JURY_LEAVE',
            'MILITARY_LEAVE',
            'PARENTAL LEAVE',
            'SICK_LEAVE_WITH_HALF_PAY',
            'WITNESS_LEAVE',
            'VOL_FIRE_EMERG_MED_ACTIV',
            'EXTENDED_SICK_LEAVE',
            'VOTING'
            );
        EXCEPTION
            WHEN duplicate_object THEN null;
END$$;
DO $$
BEGIN
    --
    -- Varying statuses for time off requests
    --
    CREATE TYPE time_off_status_type AS ENUM (
            'SAVED',
            'SUBMITTED',
            'DISAPPROVED',
            'APPROVED',
            'INVALIDATED'
            );
        EXCEPTION
            WHEN duplicate_object THEN null;
END$$;

--
-- Request ID's will start with 1 and increase by 1 for each new request
--
CREATE SEQUENCE IF NOT EXISTS request_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Table to hold time off requests
--      Each row is a single request
--
CREATE TABLE IF NOT EXISTS time_off_request (
    request_id integer NOT NULL PRIMARY KEY DEFAULT nextval('request_id_seq'::regclass),
    employee_id integer NOT NULL,
    supervisor_id integer NOT NULL,
    status time_off_status_type NOT NULL,
    update_timestamp TIMESTAMP WITHOUT TIME ZONE,
    start_date DATE,
    end_date DATE,
    active boolean
);
CREATE INDEX IF NOT EXISTS request_id_index ON time_off_request (request_id);
CREATE INDEX IF NOT EXISTS employee_id_index ON time_off_request (employee_id);
CREATE INDEX  IF NOT EXISTS supervisor_id_index ON time_off_request (supervisor_id);

--
-- Table to hold days of each request
--      Each row is a separate day from a request,
--      the days are linked back to the request
--      with the foreign key 'request_id'
--
CREATE TABLE IF NOT EXISTS time_off_request_day (
    request_id integer references time_off_request(request_id),
    request_date DATE NOT NULL,
    work_hours integer,
    holiday_hours integer,
    vacation_hours integer,
    personal_hours integer,
    sick_emp_hours integer,
    sick_fam_hours integer,
    misc_hours integer,
    misc_type time_off_misc_type,
    PRIMARY KEY (request_id, request_date),

    --Misc type must be specified if they use misc hours
    CONSTRAINT misc_check CHECK (
        misc_type IS NOT NULL
        OR misc_hours = 0
    )
);

--
-- Table to hold the comment thread associated with a
-- time off request
--      Each row holds one comment from the comment thread,
--      The comments are linked back to the request with the
--      foreign key 'request_id'
--      The comments are put in order by the num_in_thread attribute
--
CREATE TABLE IF NOT EXISTS time_off_request_comment (
    comment VARCHAR(1000),
    author_id integer NOT NULL,
    time_stamp TIMESTAMP NOT NULL,
    request_id integer references time_off_request(request_id),
    PRIMARY KEY (request_id, time_stamp)
);
