package gov.nysenate.ess.core.dao.period;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlHolidayQuery implements BasicSqlQuery
{
    GET_HOLIDAY_BASE(
        "SELECT *\n" +
        "FROM ${masterSchema}.SASSHD17691\n" +
        "WHERE CDSTATUS = 'A'"
    ),
    GET_SINGLE_HOLIDAY_SQL(
        GET_HOLIDAY_BASE.getSql() + " AND DTHOLIDAY = :date"
    ),
    GET_HOLIDAYS_SQL(
        GET_HOLIDAY_BASE.getSql() + " AND DTHOLIDAY BETWEEN :startDate AND :endDate"
    ),
    GET_NON_QUESTIONABLE_HOLIDAYS_SQL(
        GET_HOLIDAYS_SQL.getSql() + " AND cdquest = 'N'"
    );

    private String sql;

    SqlHolidayQuery(String sql) {
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