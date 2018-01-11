package gov.nysenate.ess.core.dao.policy;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPolicyQuery implements BasicSqlQuery {

    GET_ALL_ACTIVE_POLICIES_SQL(
        "SELECT * FROM ess.policy \n" +
                "WHERE active = true"
    ),

    GET_POLICY_BY_ID_SQL(
            "SELECT * FROM ess.policy \n" +
                    "WHERE id = :policyId"
    ),

    INSERT_POLICY_SQL(
        "INSERT INTO ess.policy (title, link, active, effective_date_time)\n" +
                "VALUES (?, ?, ?, ?)"
    ),

    GET_ALL_ACKNOWLEDGEMENTS(
            "SELECT * FROM ess.acknowledgement"
    ),

    GET_ACK_BY_ID(
            "SELECT * FROM ess.acknowledgement \n" +
                    "WHERE emp_id = ? AND policy_id = ?"
    ),

    INSERT_ACK_SQL(
        "INSERT INTO ess.acknowledgement (emp_id, policy_id, timestamp)\n" +
                "VALUES (2,1,'1/11/18')"
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
