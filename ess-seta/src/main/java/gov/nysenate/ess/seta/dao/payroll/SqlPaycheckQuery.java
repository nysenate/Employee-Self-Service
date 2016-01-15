package gov.nysenate.ess.seta.dao.payroll;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPaycheckQuery implements BasicSqlQuery
{
    GET_EMPLOYEE_PAYCHECKS_BY_YEAR(
           "SELECT m.NUXREFEM, m.MONET, m.MOGROSS, m.MOCHECKAMT, m.MOADVICEAMT, m.NUPERIOD, m.DTCHECK," +
           "l.CDDEDUCTION, l.DEDEDUCTIONF, d.MODEDUCTION\n" +
           "from ${tsSchema}.PM25SALLEDG m join ${tsSchema}.PD25SALLEDG d on m.nuxrefem = d.nuxrefem and m.dtcheck = d.dtcheck\n" +
           "join ${tsSchema}.PL25DEDUCTCD l on d.CDDEDUCTION = l.CDDEDUCTION\n" +
           "where m.NUXREFEM = :empId and EXTRACT(YEAR FROM m.DTCHECK) = :year and d.cdstatus = 'A'"
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
