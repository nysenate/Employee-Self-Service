package gov.nysenate.ess.core.dao.period;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPayPeriodQuery implements BasicSqlQuery
{
    GET_PAY_PERIOD_SQL(
        "SELECT * FROM ${masterSchema}.SL16PERIOD \n" +
        "WHERE CDPERIOD = :periodType AND TRUNC(:date) BETWEEN DTBEGIN AND DTEND"
    ),
    GET_PAY_PERIODS_IN_RANGE_SQL(
        "SELECT * FROM ${masterSchema}.SL16PERIOD\n" +
        "WHERE CDPERIOD = :periodType AND (DTBEGIN >= TRUNC(:startDate) OR TRUNC(:startDate) BETWEEN DTBEGIN AND DTEND)\n" +
        "                             AND (DTEND <= TRUNC(:endDate) OR TRUNC(:endDate) BETWEEN DTBEGIN AND DTEND)\n"
    ),
    GET_OPEN_ATTEND_PERIODS_SQL(
        "SELECT * FROM ${masterSchema}.SL16PERIOD \n" +
        "WHERE (DTEND <= TRUNC(:endDate) OR TRUNC(:endDate) BETWEEN DTBEGIN AND DTEND) \n" +
        "AND CDPERIOD = 'AF' AND DTPERIODYEAR >= (\n" +
        "  SELECT DISTINCT MIN(DTPERIODYEAR)\n" +
        "  FROM PM23ATTEND WHERE NUXREFEM = :empId AND DTCLOSE IS NULL\n" +
        ")"
    );

    private String sql;

    SqlPayPeriodQuery(String sql) {
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