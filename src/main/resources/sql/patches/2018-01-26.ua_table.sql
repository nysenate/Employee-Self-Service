
SET search_path = ess ;

CREATE TABLE user_agent (
  id SERIAL PRIMARY KEY,
  login_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  emp_id INT NOT NULL,
  user_agent TEXT
);

CREATE INDEX user_agent_emp_id_user_agent_index ON user_agent(emp_id, user_agent);
CREATE INDEX user_agent_emp_id_login_time_index ON user_agent(emp_id, login_time);

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ess TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ess TO PUBLIC;
