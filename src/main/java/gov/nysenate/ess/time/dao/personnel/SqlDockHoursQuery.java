package gov.nysenate.ess.time.dao.personnel;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlDockHoursQuery implements BasicSqlQuery
{
    /**
     * @author  Brian Heitner
     *
     * This query returns a listing of all Docked Hours for an employee given a specified date range.
     */

    GET_PERIOD_DOCK_HOURS(
            " SELECT acc.NUXREFEM, acc.DTPERIODYEAR AS YEAR,\n" +
                    " per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD,\n" +
                    " SUM(NVL(acc.NUHRSDOCK, 0)) OVER (PARTITION BY acc.NUXREFEM, acc.DTPERIODYEAR ORDER BY acc.DTEND) AS DOCKED_HOURS\n" +
                    " FROM ${masterSchema}.PD23ACCUSAGE acc\n" +
                    " JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF') per ON acc.DTEND = per.DTEND\n" +
                    "WHERE acc.NUXREFEM = :empId AND acc.DTEND <= :endDate\n" +
                    "AND acc.DTEND >= :startDate"
    ),

    GET_PERIOD_DOCK_HOURS_UPDATED_SINCE(
            " WITH all_rows as (SELECT acc.NUXREFEM, acc.DTPERIODYEAR AS YEAR,\n" +
                    " per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD,\n" +
                    " SUM(NVL(acc.NUHRSDOCK, 0)) OVER (PARTITION BY acc.NUXREFEM, acc.DTPERIODYEAR ORDER BY acc.DTEND) AS DOCKED_HOURS\n" +
                    " FROM ${masterSchema}.PD23ACCUSAGE acc\n" +
                    " JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF') per ON acc.DTEND = per.DTEND\n" +
                    "WHERE acc.NUXREFEM = :empId AND acc.DTEND < :beforeDate\n" +
                    "  )" +
                    " SELECT acc.NUXREFEM, acc.DTPERIODYEAR AS YEAR,\n" +
                    " per.CDPERIOD, per.CDSTATUS, per.DTBEGIN, per.DTEND, per.DTPERIODYEAR, per.NUPERIOD,\n" +
                    " alr.DOCKED_HOURS\n" +
                    " FROM ${masterSchema}.PD23ACCUSAGE acc\n" +
                    " JOIN (SELECT * FROM ${masterSchema}.SL16PERIOD WHERE CDPERIOD = 'AF') per ON acc.DTEND = per.DTEND\n" +
                    " JOIN all_rows alr ON (alr.NUXREFEM = acc.NUXREFEM and alr.dtend = acc.dtend) \n" +
                    "WHERE acc.NUXREFEM = :empId AND acc.DTEND <= :endDate\n" +
                    "AND acc.DTEND >= :startDate\n" +
                    "AND acc.DTTXNUPDATE > :fromDate\n"
    );

    private String sql;

    SqlDockHoursQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.ORACLE_10g;
    }
}