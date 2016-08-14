package gov.nysenate.ess.time.dao.payroll;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlMiscLeaveQuery implements BasicSqlQuery {

    GET_MISC_LEAVE_GRANTS(
        "SELECT * FROM ${masterSchema}.PM23MISCPRM WHERE CDSTATUS = 'A' AND NUXREFEM = :empId"
    ),
    ;

    private String sql;

    SqlMiscLeaveQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE_10g;
    }
}
