package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlTimeEntryQuery implements BasicSqlQuery
{
    SELECT_TIME_ENTRY_BY_TIME_ENTRY_ID(
        "SELECT * FROM ${tsSchema}.PD23TIMESHEET\n" +
        "WHERE CDSTATUS = :status AND NUXRDAY = :tSDayId "
    ),
    SELECT_TIME_ENTRIES_BY_TIME_RECORD_ID(
        "SELECT * FROM ${tsSchema}.PD23TIMESHEET\n" +
        "WHERE CDSTATUS = :status AND NUXRTIMESHEET = :timesheetId "
    ),
    INSERT_TIME_ENTRY(
        "INSERT INTO ${tsSchema}.PD23TIMESHEET\n" +
        " (NUXRTIMESHEET, NUXREFEM, DTDAY, NUWORK, NUTRAVEL, NUHOLIDAY, NUSICKEMP, NUSICKFAM,\n" +
        "       NUMISC, NUXRMISC, CDSTATUS, DECOMMENTS, CDPAYTYPE, NUVACATION, NUPERSONAL,\n" +
        "       NATXNORGUSER, NATXNUPDUSER, NAUSER)\n" +
        "VALUES (:timesheetId, :empId, :dayDate, :workHR, :travelHR, :holidayHR, :sickEmpHR, :sickFamilyHR, \n" +
        "        :miscHR, :miscTypeId, :status, :empComment, :payType, :vacationHR, :personalHR, \n" +
        "        :lastUser, :lastUser, :lastUser )"
    ),
    UPDATE_TIME_ENTRY(
        "UPDATE ${tsSchema}.PD23TIMESHEET " + "\n" +
        "SET NUXRTIMESHEET = :timesheetId, NUWORK = :workHR, NUTRAVEL = :travelHR,\n" +
        "   NUHOLIDAY = :holidayHR, NUSICKEMP = :sickEmpHR, NUSICKFAM = :sickFamilyHR, NUMISC = :miscHR,\n" +
        "   NUXRMISC = :miscTypeId, CDSTATUS = :status, DECOMMENTS = :empComment,\n" +
        "   CDPAYTYPE = :payType, NUVACATION = :vacationHR, NUPERSONAL = :personalHR,\n" +
        "   NATXNUPDUSER = :lastUser, NAUSER = :lastUser\n" +
        "WHERE NUXREFEM = :empId AND NUXRDAY = :tSDayId"
    );

    private String sql;

    SqlTimeEntryQuery(String sql) {
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
