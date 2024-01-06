package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlAttendanceQuery implements BasicSqlQuery {

    GET_OPEN_ATTENDANCE_YEARS(
        "SELECT DISTINCT year\n" +
        "FROM (\n" +
        "  WITH current_year AS (\n" +
        "      SELECT\n" +
        "        EXTRACT(YEAR FROM SYSDATE)                 AS year,\n" +
        "        TRUNC(SYSDATE, 'YEAR')                     AS year_start,\n" +
        "        ADD_MONTHS(TRUNC(SYSDATE, 'YEAR'), 12) - 1 AS year_end\n" +
        "      FROM DUAL\n" +
        "  )\n" +
        // Get years for open master attendance records
        "  SELECT TO_NUMBER(DTPERIODYEAR) AS year\n" +
        "  FROM ${masterSchema}.PM23ATTEND\n" +
        "  WHERE CDSTATUS = 'A'\n" +
        "        AND NUXREFEM = :empId\n" +
        "        AND (DTCLOSE IS NULL OR DTCLOSE > SYSDATE)\n" +
        "  UNION\n" +
        // Get the current year if the employee is currently active
        "  SELECT year\n" +
        "  FROM current_year, ${masterSchema}.PM21PERSONN pers\n" +
        "  WHERE pers.NUXREFEM = :empId AND pers.CDEMPSTATUS = 'A'\n" +
        "  UNION\n" +
        // Or get the current year if the employee was terminated during the year
        "  SELECT year\n" +
        "  FROM current_year, ${masterSchema}.PM21PERAUDIT aud, ${masterSchema}.PD21PTXNCODE code\n" +
        "  WHERE aud.CDSTATUS = 'A' AND code.CDSTATUS = 'A'\n" +
        "        AND code.CDTRANS = 'EMP'\n" +
        "        AND aud.NUCHANGE = code.NUCHANGE\n" +
        "        AND aud.NUXREFEM = :empId\n" +
        "        AND aud.CDSTATPER != 'RETD'\n" +
        "        AND code.DTEFFECT BETWEEN year_start AND year_end\n" +
        ")"
    ),
    GET_ALL_ATTENDANCE_YEARS(
        "SELECT DTPERIODYEAR\n" +
        "FROM ${masterSchema}.PM23ATTEND\n" +
        "WHERE NUXREFEM = :empId\n" +
        "ORDER BY DTPERIODYEAR ASC"
    ),
    GET_ATTENDANCE_RECORDS_SELECT(
        "SELECT rec.*\n" +
        "FROM ${masterSchema}.PM23ATTEND year\n" +
        "RIGHT JOIN ${masterSchema}.PD23ATTEND rec\n" +
        "   ON year.NUXREFEM = rec.NUXREFEM \n" +
        "   AND year.DTPERIODYEAR = rec.DTPERIODYEAR\n" +
        "WHERE rec.CDSTATUS = 'A'\n"
    ),
    GET_OPEN_ATTENDANCE_RECORDS(
        GET_ATTENDANCE_RECORDS_SELECT.getSql() + "\n" +
        "   AND (year.DTCLOSE IS NULL OR year.DTCLOSE > SYSDATE)"
    ),
    GET_OPEN_ATTENDANCE_RECORDS_FOR_EMPID(
        GET_OPEN_ATTENDANCE_RECORDS.getSql() + "\n" +
        "   AND rec.NUXREFEM = :empId"
    ),

    GET_ATTENDANCE_RECORDS_FOR_YEAR (
        GET_ATTENDANCE_RECORDS_SELECT.getSql() + "\n" +
        "   AND rec.NUXREFEM = :empId AND rec.DTPERIODYEAR = :year"
    ),

    GET_ATTENDANCE_RECORDS_FOR_DATES (
        GET_ATTENDANCE_RECORDS_SELECT.getSql() + "\n" +
        "   AND rec.NUXREFEM = :empId\n" +
        "   AND (rec.DTBEGIN BETWEEN :startDate AND :endDate\n" +
        "       OR rec.DTEND BETWEEN :startDate AND :endDate)"
    ),

    GET_ATTENDANCE_RECORD (
        GET_ATTENDANCE_RECORDS_SELECT.getSql() + "\n" +
        "  AND rec.NUXREFEM = :empId\n" +
        "  AND rec.DTEND = :endDate"
    );

    private final String sql;

    SqlAttendanceQuery(String sql) {
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
