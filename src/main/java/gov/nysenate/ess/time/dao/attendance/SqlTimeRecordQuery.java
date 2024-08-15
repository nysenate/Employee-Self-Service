package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlTimeRecordQuery implements BasicSqlQuery {
    TIME_RECORD_COLUMNS(
            /**   PM23TIMESHEET columns (no alias needed) */
            "    rec.NUXRTIMESHEET, rec.NUXREFEM, rec.NATXNORGUSER, rec.NATXNUPDUSER, rec.NAUSER, rec.DTTXNORIGIN, rec.DTTXNUPDATE,\n" +
            "    rec.CDSTATUS, rec.CDTSSTAT, rec.DTBEGIN, rec.DTEND, rec.DEREMARKS, rec.NUXREFSV, rec.DEEXCEPTION,\n" +
            "    rec.DTPROCESS, rec.CDRESPCTRHD, rec.CDPAYTYPE, rec.NUXREFAPR,\n" +
            /**   SL16PERIOD columns (aliased with PER_) */
            "    per.DTBEGIN AS PER_DTBEGIN, per.DTEND AS PER_DTEND, per.CDSTATUS AS PER_CDSTATUS, per.CDPERIOD AS PER_CDPERIOD,\n" +
            "    per.NUPERIOD AS PER_NUPERIOD, per.DTPERIODYEAR AS PER_DTPERIODYEAR,\n" +
            /**   PD23TIMESHEET columns (aliased with ENT_) */
            "    ent.NUXRDAY AS ENT_NUXRDAY, ent.NUXRTIMESHEET, ent.NUXRTIMESHEET AS ENT_NUXRTIMESHEET, ent.NUXREFEM AS ENT_NUXREFEM,\n" +
            "    ent.DTDAY AS ENT_DTDAY, ent.NUWORK AS ENT_NUWORK, ent.NUTRAVEL AS ENT_NUTRAVEL, ent.NUHOLIDAY AS ENT_NUHOLIDAY,\n" +
            "    ent.NUVACATION AS ENT_NUVACATION, ent.NAUSER AS ENT_NAUSER, ent.NUPERSONAL AS ENT_NUPERSONAL, ent.NUSICKEMP AS ENT_NUSICKEMP,\n" +
            "    ent.NUSICKFAM AS ENT_NUSICKFAM, ent.NUMISC AS ENT_NUMISC, ent.NUXRMISC AS ENT_NUXRMISC,\n" +
            "    ent.NUMISC2 AS ENT_NUMISC2, ent.NUXRMISC2 AS ENT_NUXRMISC2, ent.NATXNORGUSER AS ENT_NATXNORGUSER,\n" +
            "    ent.NATXNUPDUSER AS ENT_NATXNUPDUSER, ent.DTTXNORIGIN AS ENT_DTTXNORIGIN, ent.DTTXNUPDATE AS ENT_DTTXNUPDATE,\n" +
            "    ent.CDSTATUS AS ENT_CDSTATUS, ent.DECOMMENTS AS ENT_DECOMMENTS, ent.CDPAYTYPE AS ENT_CDPAYTYPE "
    ),
    GET_TIME_REC_SQL_TEMPLATE(
            "SELECT \n" + TIME_RECORD_COLUMNS.getSql() + "\n" +
            "FROM ${tsSchema}.PM23TIMESHEET rec\n" +
            "JOIN ${masterSchema}.SL16PERIOD per\n" +
            "    ON rec.DTBEGIN BETWEEN per.DTBEGIN AND per.DTEND\n" +
            "LEFT JOIN ${masterSchema}.PM23ATTEND att\n" +
            "    ON rec.NUXREFEM = att.NUXREFEM AND per.DTPERIODYEAR = att.DTPERIODYEAR\n" +
            "LEFT JOIN ${tsSchema}.PD23TIMESHEET ent\n" +
            "    ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET\n" +
            "        AND ent.CDSTATUS = 'A'\n" +
            "WHERE per.CDSTATUS = 'A' AND rec.CDSTATUS = 'A'\n" +
            "    AND per.CDPERIOD = 'AF'\n"
    ),
    GET_TIME_REC_BY_ID(
            "SELECT \n" + TIME_RECORD_COLUMNS.getSql() + "\n" +
            "FROM ${tsSchema}.PM23TIMESHEET rec\n" +
            "JOIN ${masterSchema}.SL16PERIOD per\n" +
            "    ON rec.DTBEGIN BETWEEN per.DTBEGIN AND per.DTEND\n" +
            "LEFT JOIN ${tsSchema}.PD23TIMESHEET ent\n" +
            "    ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET\n" +
            "        AND ent.CDSTATUS = 'A'\n" +
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
            GET_TIME_REC_BY_DATES.getSql() + "    AND rec.NUXREFEM IN (:empIds)\n"
    ),
    GET_ACTIVE_TIME_REC(
            GET_TIME_REC_SQL_TEMPLATE.getSql() +
                    "    AND (att.DTCLOSE IS NULL OR att.DTCLOSE > SYSDATE)\n"
    ),
    GET_ACTIVE_TIME_REC_BY_EMP_ID(
            GET_ACTIVE_TIME_REC.getSql() +
                    "    AND rec.NUXREFEM = :empId\n"
    ),

    GET_LAST_UPDATE_DATE_TIME(
            "SELECT GREATEST(\n" +
            "   CAST (MAX(rec.DTTXNUPDATE) AS TIMESTAMP),\n" +
            "   CAST (NVL(MAX(ent.DTTXNUPDATE), '01-JAN-70') AS TIMESTAMP)\n" +
            ") AS MAX_DTTXNUPDATE\n" +
            "FROM ${tsSchema}.PM23TIMESHEET rec\n" +
            "LEFT JOIN ${tsSchema}.PD23TIMESHEET ent\n" +
            "  ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET\n"
    ),

    GET_UPDATED_TIME_RECS(
            "WITH updated_ids AS (\n" +
            "  SELECT rec.NUXRTIMESHEET\n" +
            "  FROM ${tsSchema}.PM23TIMESHEET rec\n" +
            "  WHERE rec.DTTXNUPDATE BETWEEN :startDateTime AND :endDateTime\n" +
            "  UNION ALL\n" +
            "  SELECT ent.NUXRTIMESHEET\n" +
            "  FROM ${tsSchema}.PD23TIMESHEET ent\n" +
            "  WHERE ent.DTTXNUPDATE BETWEEN :startDateTime AND :endDateTime\n" +
            ")\n" +
            "SELECT\n" +
            TIME_RECORD_COLUMNS.getSql() + "\n" +
            "FROM ${tsSchema}.PM23TIMESHEET rec\n" +
            "JOIN ${masterSchema}.SL16PERIOD per\n" +
            "    ON rec.DTBEGIN BETWEEN per.DTBEGIN AND per.DTEND\n" +
            "       AND per.CDPERIOD = 'AF'\n" +
            "JOIN updated_ids\n" +
            "    on rec.NUXRTIMESHEET = updated_ids.NUXRTIMESHEET\n" +
            "LEFT JOIN ${tsSchema}.PD23TIMESHEET ent\n" +
            "    ON rec.NUXRTIMESHEET = ent.NUXRTIMESHEET AND ent.CDSTATUS = 'A'" +
            "WHERE per.CDSTATUS = 'A'\n"
    ),

    GET_TREC_DISTINCT_YEARS(
            "SELECT DISTINCT EXTRACT(YEAR FROM DTEND) AS year\n" +
            "FROM ${tsSchema}.PM23TIMESHEET\n" +
            "WHERE NUXREFEM = :empId\n"
    ),

    GET_SUP_TREC_COUNT(
            "SELECT COUNT(*) AS record_count\n" +
            "FROM ${tsSchema}.PM23TIMESHEET\n" +
            "WHERE NUXREFSV = :supId\n" +
            "   AND CDTSSTAT != 'AP'"
    ),
    GET_EXISTING_TREC_ID(
            "SELECT NUXRTIMESHEET, CDTSSTAT\n" +
            "FROM ${tsSchema}.PM23TIMESHEET ts\n" +
            "WHERE NUXREFEM = :empId\n" +
            "  AND DTBEGIN <= :endDate\n" +
            "  AND DTEND >= :beginDate"
    ),

    INSERT_TIME_REC(
            "INSERT \n" +
            "INTO ${tsSchema}.PM23TIMESHEET \n" +
            "(NUXREFEM, CDSTATUS, CDTSSTAT, DTBEGIN, DTEND, DEREMARKS, NUXREFSV, DEEXCEPTION, DTPROCESS, CDRESPCTRHD, nuxrefapr) \n" +
            "VALUES (:empId, :status, :tSStatusId, :beginDate, :endDate, :remarks, :supervisorId, :excDetails, :procDate, :respCtr, :approvalEmpId) \n"
    ),


    UPDATE_TIME_REC_SQL(
            "UPDATE ${tsSchema}.PM23TIMESHEET \n" +
            "SET \n" +
            "  NUXREFEM = :empId, CDSTATUS = :status, CDTSSTAT = :tSStatusId,\n" +
            "  DTBEGIN = :beginDate, DTEND = :endDate, DEREMARKS = :remarks, NUXREFSV = :supervisorId,\n" +
            "  DEEXCEPTION = :excDetails, DTPROCESS = :procDate, NAUSER = :lastUser, CDRESPCTRHD = :respCtr,\n" +
            "  NUXREFAPR = :approvalEmpId\n" +
            "WHERE NUXRTIMESHEET = :timesheetId\n" +
            "  AND CDTSSTAT != 'AP'"
    ),


    DELETE_TIME_REC_SQL(
            "DELETE FROM ${tsSchema}.PM23TIMESHEET WHERE NUXRTIMESHEET = :timesheetId"
    ),
    DELETE_TIME_REC_ENTRIES_SQL(
            "DELETE FROM ${tsSchema}.PD23TIMESHEET WHERE NUXRTIMESHEET = :timesheetId"
    );

    private final String sql;

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
