package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlAttendanceQuery implements BasicSqlQuery {

    GET_OPEN_ATTENDANCE_YEARS(
        "SELECT CAST(DTPERIODYEAR AS INTEGER) AS DTPERIODYEAR\n" +
        "FROM ${masterSchema}.PM23ATTEND\n" +
        "WHERE CDSTATUS = 'A'\n" +
        "  AND NUXREFEM = :empId\n" +
        "  AND (DTCLOSE IS NULL OR DTCLOSE > SYSDATE)\n" +

        "UNION\n" +
        // Select the current year if the last year was a valid attendance year
        // and the current year's record doesn't exist yet
        "SELECT last.DTPERIODYEAR + 1 AS DEPERIODYEAR\n" +
        "FROM ${masterSchema}.PM23ATTEND last\n" +
        "LEFT JOIN ${masterSchema}.PM23ATTEND curr\n" +
        "  ON last.DTPERIODYEAR = curr.DTPERIODYEAR - 1\n" +
        "  AND last.NUXREFEM = curr.NUXREFEM\n" +
        "WHERE last.CDSTATUS = 'A'\n" +
        "  AND last.NUXREFEM = :empId\n" +
        "  AND last.DTPERIODYEAR = EXTRACT(YEAR FROM SYSDATE) - 1\n" +
        "  AND curr.CDSTATUS IS NULL\n" +
        "ORDER BY DTPERIODYEAR ASC"
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
        "WHERE rec.CDSTATUS = 'A'\n" +
        "   AND (year.CDSTATUS IS NULL OR year.CDSTATUS = 'A')"
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
    ;

    private String sql;

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
