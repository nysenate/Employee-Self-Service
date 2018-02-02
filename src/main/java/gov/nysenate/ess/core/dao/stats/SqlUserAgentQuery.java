package gov.nysenate.ess.core.dao.stats;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlUserAgentQuery implements BasicSqlQuery {

    INSERT_USER_AGENT_INFO(
            "INSERT INTO ${essSchema}.user_agent\n" +
            "       ( emp_id, login_time, user_agent)\n" +
            "VALUES (:empId, :loginTime, :userAgent)"
    ),
    ;

    private String sql;

    SqlUserAgentQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }

}
