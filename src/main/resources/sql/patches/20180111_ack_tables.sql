CREATE TABLE ess.policy (
  policy_id   SERIAL PRIMARY KEY,
  title       VARCHAR(250),
  filename        VARCHAR(250),
  active      BOOLEAN,
  effective_date_time       VARCHAR(40)
);

CREATE TABLE ess.acknowledgement (
  emp_id      INTEGER,
  policy_id    INTEGER REFERENCES ess.policy,
  timestamp       VARCHAR(40)
);

ALTER TABLE ess.acknowledgement ADD PRIMARY KEY (emp_id,policy_id);