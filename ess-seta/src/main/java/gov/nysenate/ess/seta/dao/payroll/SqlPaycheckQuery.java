package gov.nysenate.ess.seta.dao.payroll;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPaycheckQuery implements BasicSqlQuery
{
    GET_EMPLOYEE_PAYCHECKS_BY_YEAR(
           "SELECT m.NUXREFEM, m.MONET, m.MOGROSS, m.MOCHECKAMT, m.MOADVICEAMT, m.NUPERIOD, m.DTCHECK,\n" +
           "    l.CDDEDUCTION, l.DEDEDUCTIONF, d.MODEDUCTION\n" +
           "FROM ${masterSchema}.PM25SALLEDG m\n" +
           "JOIN ${tsSchema}.PD25SALLEDG d\n" +
           "    ON m.NUXREFEM = d.NUXREFEM AND m.DTCHECK = d.DTCHECK\n" +
           "JOIN ${masterSchema}.PL25DEDUCTCD l\n" +
           "    ON d.CDDEDUCTION = l.CDDEDUCTION\n" +
           "WHERE m.NUXREFEM = :empId AND EXTRACT(YEAR FROM m.DTCHECK) = :year AND d.CDSTATUS = 'A'"
    );

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
