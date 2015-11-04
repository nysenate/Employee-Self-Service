package gov.nysenate.ess.web.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlTimeRecordQuery implements BasicSqlQuery
{
    TIME_RECORD_COLUMNS(
        /**   PM23TIMESHEET columns (no alias needed) */
        "    rec.NUXRTIMESHEET, rec.NUXREFEM, rec.NATXNORGUSER, rec.NATXNUPDUSER, rec.NAUSER, rec.DTTXNORIGIN, rec.DTTXNUPDATE,\n" +
        "    rec.CDSTATUS, rec.CDTSSTAT, rec.DTBEGIN, rec.DTEND, rec.DEREMARKS, rec.NUXREFSV, rec.DEEXCEPTION,\n" +
        "    rec.DTPROCESS, rec.CDRESPCTRHD, rec.CDPAYTYPE,\n" +
        /**   SL16PERIOD columns (aliased with PER_) */
        "    per.DTBEGIN AS PER_DTBEGIN, per.DTEND AS PER_DTEND, per.CDSTATUS AS PER_CDSTATUS, per.CDPERIOD AS PER_CDPERIOD,\n" +
        "    per.NUPERIOD AS PER_NUPERIOD, per.DTPERIODYEAR AS PER_DTPERIODYEAR,\n" +
        /**   PD23TIMESHEET columns (aliased with ENT_) */
        "    ent.NUXRDAY AS ENT_NUXRDAY, ent.NUXRTIMESHEET, ent.NUXRTIMESHEET AS ENT_NUXRTIMESHEET, ent.NUXREFEM AS ENT_NUXREFEM,\n" +
        "    ent.DTDAY AS ENT_DTDAY, ent.NUWORK AS ENT_NUWORK, ent.NUTRAVEL AS ENT_NUTRAVEL, ent.NUHOLIDAY AS ENT_NUHOLIDAY,\n" +
        "    ent.NUVACATION AS ENT_NUVACATION, ent.NAUSER AS ENT_NAUSER, ent.NUPERSONAL AS ENT_NUPERSONAL, ent.NUSICKEMP AS ENT_NUSICKEMP,\n" +
        "    ent.NUSICKFAM AS ENT_NUSICKFAM, ent.NUMISC AS ENT_NUMISC, ent.NUXRMISC AS ENT_NUXRMISC, ent.NATXNORGUSER AS ENT_NATXNORGUSER,\n" +
        "    ent.NATXNUPDUSER AS ENT_NATXNUPDUSER, ent.DTTXNORIGIN AS ENT_DTTXNORIGIN, ent.DTTXNUPDATE AS ENT_DTTXNUPDATE,\n" +
        "    ent.CDSTATUS AS ENT_CDSTATUS, ent.DECOMMENTS AS ENT_DECOMMENTS, ent.CDPAYTYPE AS ENT_CDPAYTYPE "
    ),
    GET_TIME_REC_SQL_TEMPLATE(
        "SELECT \n" + TIME_RECORD_COLUMNS.getSql() + "\n" +
        "FROM ${masterSchema}.PM23ATTEND att\n" +
        "JOIN ${masterSchema}.SL16PERIOD per\n" +
        "    ON att.DTPERIODYEAR = per.DTPERIODYEAR\n" +
        "JOIN ${tsSchema}.PM23TIMESHEET rec\n" +
        "    ON rec.NUXREFEM = att.NUXREFEM AND rec.DTBEGIN BETWEEN per.DTBEGIN AND per.DTEND\n" +
        "LEFT JOIN ${tsSchema}.PD23TIMESHEET ent\n" +
        "    ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET AND ent.CDSTATUS = 'A'\n" +
        "WHERE att.CDSTATUS = 'A' AND per.CDSTATUS = 'A' AND rec.CDSTATUS = 'A'\n" +
        "    AND per.CDPERIOD = 'AF'\n"
    ),
    GET_TIME_REC_BY_ID(
        "SELECT \n" + TIME_RECORD_COLUMNS.getSql() + "\n" +
        "FROM ${tsSchema}.PM23TIMESHEET rec\n" +
        "JOIN ${masterSchema}.SL16PERIOD per\n" +
        "    ON rec.DTBEGIN BETWEEN per.DTBEGIN AND per.DTEND\n" +
        "LEFT JOIN ${tsSchema}.PD23TIMESHEET ent\n" +
        "    ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET AND ent.CDSTATUS = 'A'\n" +
        "WHERE per.CDSTATUS = 'A' AND per.CDPERIOD = 'AF'\n" +
        "   AND rec.NUXRTIMESHEET = :timesheetId"
    ),
    GET_TIME_REC_BY_DATES(
        GET_TIME_REC_SQL_TEMPLATE.getSql() +
        "    AND (rec.DTBEGIN BETWEEN :startDate AND :endDate\n" +
        "        OR rec.DTEND BETWEEN :startDate AND :endDate)\n" +
        "    AND rec.CDTSSTAT IN (:statuses)\n"
    ),
    GET_TIME_REC_BY_DATES_EMP_ID(
        GET_TIME_REC_BY_DATES.getSql() + "    AND att.NUXREFEM IN (:empIds)\n"
    ),
    GET_ACTIVE_TIME_REC(
        GET_TIME_REC_SQL_TEMPLATE.getSql() +
        "    AND (att.DTCLOSE IS NULL OR att.DTCLOSE > SYSDATE)\n"
    ),
    GET_ACTIVE_TIME_REC_BY_EMP_IDS(
        GET_ACTIVE_TIME_REC.getSql() +
        "    AND (att.NUXREFEM IN (:empIds))\n"
    ),

    GET_TREC_DISTINCT_YEARS(
        "SELECT DISTINCT EXTRACT(YEAR FROM DTEND) AS year\n" +
        "FROM ${tsSchema}.PM23TIMESHEET\n" +
        "WHERE NUXREFEM = :empId\n"
    ),

    INSERT_TIME_REC(
        "INSERT \n" +
        "INTO ${tsSchema}.PM23TIMESHEET \n" +
        "(NUXREFEM, CDSTATUS, CDTSSTAT, DTBEGIN, DTEND, DEREMARKS, NUXREFSV, DEEXCEPTION, DTPROCESS, CDRESPCTRHD) \n" +
        "VALUES (:empId, :status, :tSStatusId, :beginDate, :endDate, :remarks, :supervisorId, :excDetails, :procDate, :respCtr) \n"
    ),


    UPDATE_TIME_REC_SQL (
        "UPDATE ${tsSchema}.PM23TIMESHEET \n" +
        "SET \n" +
        "  NUXREFEM = :empId, CDSTATUS = :status, CDTSSTAT = :tSStatusId,\n" +
        "  DTBEGIN = :beginDate, DTEND = :endDate, DEREMARKS = :remarks, NUXREFSV = :supervisorId,\n" +
        "  DEEXCEPTION = :excDetails, DTPROCESS = :procDate, NAUSER = :lastUpdater, CDRESPCTRHD = :respCtr\n" +
        "WHERE NUXRTIMESHEET = :timesheetId"
    ),


    DELETE_TIME_REC_SQL (
        "DELETE FROM ${tsSchema}.PM23TIMESHEET WHERE NUXRTIMESHEET = :timesheetId"
    ),
    DELETE_TIME_REC_ENTRIES_SQL (
        "DELETE FROM ${tsSchema}.PD23TIMESHEET WHERE NUXRTIMESHEET = :timesheetId"
    ),
    ;

    private String sql;

    SqlTimeRecordQuery(String sql) {
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
