package gov.nysenate.ess.core.dao.policy;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPolicyQuery implements BasicSqlQuery {

    GET_ALL_ACTIVE_POLICIES_SQL(
        "SELECT * FROM ${essSchema}.policy \n" +
                "WHERE active = true"
    ),

    GET_POLICY_BY_ID_SQL(
            "SELECT * FROM ${essSchema}.policy \n" +
                    "WHERE id = :policyId"
    ),

    INSERT_POLICY_SQL(
        "INSERT INTO ${essSchema}.policy (title, filename, active, effective_date_time)\n" +
                "VALUES (:title, :filename, :active, :effectiveDateTime)"
    ),

    GET_ALL_ACKNOWLEDGEMENTS(
            "SELECT * FROM ${essSchema}.acknowledgement"
    ),

    GET_ACK_BY_ID(
            "SELECT * FROM ${essSchema}.acknowledgement \n" +
                    "WHERE emp_id = :empid AND policy_id = :policyId"
    ),

    INSERT_ACK_SQL(
        "INSERT INTO ${essSchema}.acknowledgement (emp_id, policy_id, timestamp)\n" +
                "VALUES (:empId, :policyId, :timestamp)"
    );


    private String sql;

    SqlPolicyQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
