package gov.nysenate.ess.seta.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlAttendanceQuery implements BasicSqlQuery {
    GET_OPEN_ATTENDANCE_YEARS(
        "SELECT DTPERIODYEAR\n" +
        "FROM ${masterSchema}.PM23ATTEND\n" +
        "WHERE CDSTATUS = 'A' AND NUXREFEM = :empId AND (DTCLOSE IS NULL OR DTCLOSE > SYSDATE)\n" +
        "ORDER BY DTPERIODYEAR ASC"
    ),

    GET_ATTENDANCE_RECORDS_SELECT(
        "SELECT rec.*\n" +
        "FROM ${masterSchema}.PM23ATTEND year\n" +
        "JOIN ${masterSchema}.PD23ATTEND rec ON year.NUXREFEM = rec.NUXREFEM AND year.DTPERIODYEAR = rec.DTPERIODYEAR\n" +
        "WHERE year.CDSTATUS = 'A' AND rec.CDSTATUS = 'A'"
    ),
    GET_OPEN_ATTENDANCE_RECORDS(
        GET_ATTENDANCE_RECORDS_SELECT.getSql() + "\n" +
        "   AND (year.DTCLOSE IS NULL OR year.DTCLOSE > SYSDATE)"
    ),
    GET_OPEN_ATTENDANCE_RECORDS_FOR_EMPID(
        GET_OPEN_ATTENDANCE_RECORDS.getSql() + "\n" +
        "   AND year.NUXREFEM = :empId"
    ),

    GET_ATTENDANCE_RECORDS_FOR_YEAR (
        GET_ATTENDANCE_RECORDS_SELECT.getSql() + "\n" +
        "   AND year.NUXREFEM = :empId AND year.DTPERIODYEAR = :year"
    )
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
