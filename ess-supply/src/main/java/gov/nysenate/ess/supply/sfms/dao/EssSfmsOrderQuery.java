package gov.nysenate.ess.supply.sfms.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum EssSfmsOrderQuery implements BasicSqlQuery {

    GET_RESPONSIBILITY_CENTER_HD(
            "SELECT CDRESPCTRHD FROM ${masterSchema}.SL16LOCATION\n" +
            "WHERE CDSTATUS = 'A' AND CDLOCAT = :locCode AND CDLOCTYPE = :locType"
    ),
    SEARCH_WHERE_CLAUSE(
            "WHERE CDSTATUS = 'A' AND CDRECTYPE = 'P' AND CDORGID = 'ALL' \n" +
            "AND CDLOCATFROM = 'LC100S' AND CDLOCTYPEFRM = 'P' \n" +
            "AND CDLOCATTO like :locCode AND CDLOCTYPETO like :locType AND NAISSUEDBY like :issueEmpName \n" +
            "AND DTISSUE BETWEEN :startDate AND :endDate \n"
    ),
    TOTAL_ORDERS_SUB_QUERY(
            "Select count(count(*))  from ${masterSchema}.fd12expissue\n" + SEARCH_WHERE_CLAUSE.getSql() +
            "Group by nuissue, dtissue, cdloctypefrm, cdlocatto, cdlocatfrom, cdloctypeto"
    ),
    GET_ORDERS(
            "SELECT NUISSUE, NUXREFCO, DTISSUE, DTTXNUPDATE, DTTXNORIGIN, CDLOCTYPEFRM, CDLOCATTO, CDLOCATFROM, " +
            "CDLOCTYPETO, NAISSUEDBY, NATXNORGUSER, NATXNUPDUSER, AMQTYISSUE, AMQTYISSSTD, CDISSUNIT, CDRESPCTRHD, \n" +
            "(" + TOTAL_ORDERS_SUB_QUERY.getSql() + ") as total_rows \n" +
            "FROM ${masterSchema}.FD12EXPISSUE \n" + SEARCH_WHERE_CLAUSE.getSql()

    ),
    GET_ORDER_BY_ID(
            "SELECT NUISSUE, NUXREFCO, DTISSUE, DTTXNUPDATE, DTTXNORIGIN, CDLOCTYPEFRM, CDLOCATTO, CDLOCATFROM, " +
            "CDLOCTYPETO, NAISSUEDBY, NATXNORGUSER, NATXNUPDUSER, AMQTYISSUE, AMQTYISSSTD, CDISSUNIT, CDRESPCTRHD\n" +
            "FROM ${masterSchema}.FD12EXPISSUE \n" +
            "WHERE CDSTATUS = 'A' AND CDRECTYPE = 'P' AND CDORGID = 'ALL' \n" +
            "AND CDLOCATFROM = 'LC100S' AND CDLOCTYPEFRM = 'P' AND CDLOCATTO like :locCode \n" +
            "AND CDLOCTYPETO like :locType AND NUISSUE = :nuIssue AND DTISSUE = :issueDate"
    ),
    INSERT_ORDER(
            "INSERT INTO ${masterSchema}.FD12EXPISSUE \n" +
            "(NUISSUE, NUXREFCO, DTISSUE, DTTXNUPDATE, DTTXNORIGIN, CDLOCTYPEFRM, CDLOCTYPETO, CDRECTYPE, CDSTATUS, CDLOCATFROM, \n" +
            "CDLOCATTO, NAISSUEDBY, NATXNORGUSER, NATXNUPDUSER, AMQTYISSUE, AMQTYISSSTD, \n" +
            "CDORGID, CDISSUNIT, CDRESPCTRHD) \n" +
            "SELECT :nuIssue, :itemId, :issueDate, SYSDATE, SYSDATE, 'P', :locType, 'P', 'A', 'LC100S', \n" +
            ":locCode, :issueEmpName, :completingUserUid, :completingUserUid, :quantity, AMSTDUNIT * :quantity, \n" +
            "'ALL', :unit, (" + GET_RESPONSIBILITY_CENTER_HD.getSql() + ") \n" +
            "FROM ${masterSchema}.FL12STDUNIT WHERE CDSTATUS = 'A' AND CDSTDUNIT = :unit"
    ),
    INSERT_ORDER_AUDIT(
            ""
    ),
    UPDATE_ORDER(
            ""
    ),
    UPDATE_INVENTORY(
            ""
    ),
    GET_NUISSUE(
            ""
    );

    private String sql;

    EssSfmsOrderQuery(String sql) {
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
