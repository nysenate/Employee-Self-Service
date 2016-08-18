package gov.nysenate.ess.time.dao.payroll;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPaycheckQuery implements BasicSqlQuery
{
    GET_EMPLOYEE_PAYCHECKS_BASE(
        "SELECT m.NUXREFEM, m.MONET, m.MOGROSS, m.MOCHECKAMT, m.MOADVICEAMT, m.NUPERIOD, m.DTCHECK,\n" +
        "    l.CDDEDUCTION, l.DEDEDUCTIONF, l.CDORDER, d.MODEDUCTION\n" +
        "FROM ${masterSchema}.PM25SALLEDG m\n" +
        "JOIN ${masterSchema}.PD25SALLEDG d\n" +
        "    ON m.NUXREFEM = d.NUXREFEM AND m.DTCHECK = d.DTCHECK\n" +
        "JOIN ${masterSchema}.PL25DEDUCTCD l\n" +
        "    ON d.CDDEDUCTION = l.CDDEDUCTION"
    ),
    GET_ACTIVE_PAYCHECKS_FOR_EMP(
        GET_EMPLOYEE_PAYCHECKS_BASE.getSql() + "\n" +
        "WHERE m.NUXREFEM = :empId AND d.CDSTATUS = 'A'"
    ),
    GET_EMPLOYEE_PAYCHECKS_BY_YEAR(
        GET_ACTIVE_PAYCHECKS_FOR_EMP.getSql() + "\n" +
        "    AND EXTRACT(YEAR FROM m.DTCHECK) = :year"
    ),
    GET_EMPLOYEE_PAYCHECKS_BY_DATE(
        GET_ACTIVE_PAYCHECKS_FOR_EMP.getSql() + "\n" +
        "    AND m.DTCHECK BETWEEN :beginDate AND :endDate"
    ),
    ;

    private String sql;

    SqlPaycheckQuery(String sql) {
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
