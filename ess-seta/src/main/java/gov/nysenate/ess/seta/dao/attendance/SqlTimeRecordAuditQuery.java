package gov.nysenate.ess.seta.dao.attendance;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlTimeRecordAuditQuery implements BasicSqlQuery {

    INSERT_TIMESHEET_AUDIT (
        "INSERT INTO ${tsSchema}.PM23TIMESHTAUD(\n" +
        "   NUXRTIMESHEET, NUXREFEM, NAUSER, NATXNORGUSER, NATXNUPDUSER, DTTXNORIGIN, \n" +
        "   DTTXNUPDATE, CDSTATUS, CDTSSTAT, DTBEGIN, DTEND, CDTRAVEL, CDPAYTYPE, DEREMARKS, \n" +
        "   NUXREFSV, DEEXCEPTION, CDRESPCTRHD, CDUSRSPLIT, CDCONFLOK, NUXREFAPR\n" +
        ")\n" +
        "SELECT NUXRTIMESHEET, NUXREFEM, NAUSER, NATXNORGUSER, NATXNUPDUSER, DTTXNORIGIN, \n" +
        "   DTTXNUPDATE, CDSTATUS, CDTSSTAT, DTBEGIN, DTEND, CDTRAVEL, CDPAYTYPE, DEREMARKS, \n" +
        "   NUXREFSV, DEEXCEPTION, CDRESPCTRHD, CDUSRSPLIT, CDCONFLOK, NUXREFAPR  \n" +
        "FROM PM23TIMESHEET\n" +
        "WHERE NUXRTIMESHEET = :timeRecordId"
    )

    ;

    private String sql;

    SqlTimeRecordAuditQuery(String sql) {
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
