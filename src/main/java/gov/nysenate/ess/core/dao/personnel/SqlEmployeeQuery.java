package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlEmployeeQuery implements BasicSqlQuery
{
    GET_EMP_SQL_TMPL(
        "SELECT DISTINCT \n" +
         // Personal details
        "per.*, ttl.FFDEEMPTITLL, ttl.DTTXNUPDATE AS TTL_DTTXNUPDATE, \n" +
        "addr.ADSTREET1, addr.ADSTREET2, addr.ADCITY, addr.ADSTATE, addr.ADZIPCODE, addr.DTTXNUPDATE AS ADDR_DTTXNUPDATE,\n" +
        // Employee N Number
        "xref.NUEMPLID AS NUEMPLID, xref.DTTXNUPDATE AS XREF_DTTXNUPDATE,\n" +
         // Responsibility center
        "rctr.DTEFFECTBEG AS RCTR_DTEFFECTBEG, rctr.DTEFFECTEND AS RCTR_DTEFFECTEND,\n" +
        "rctr.CDSTATUS AS RCTR_CDSTATUS, rctr.CDRESPCTR AS RCTR_CDRESPCTR,\n" +
        "rctr.DERESPCTR AS RCTR_DERESPCTR, rctr.DTTXNUPDATE AS RCTR_DTTXNUPDATE,\n" +
         // Responsibility center head
        "rctrhd.CDRESPCTRHD AS RCTRHD_CDRESPCTRHD, rctrhd.CDSTATUS AS RCTRHD_CDSTATUS, " +
        "rctrhd.CDAFFILIATE AS RCTRHD_CDAFFILIATE, rctrhd.DERESPCTRHDS AS RCTRHD_DERESPCTRHDS, \n" +
        "rctrhd.FFDERESPCTRHDF AS RCTRHD_FFDERESPCTRHDF, rctrhd.DTTXNUPDATE AS RCTRHD_DTTXNUPDATE,\n" +
         // Agency
        "agcy.CDAGENCY AS AGCY_CDAGENCY, agcy.CDSTATUS AS AGCY_CDSTATUS,\n" +
        "agcy.DEAGENCYS AS AGCY_DEAGENCYS, agcy.DEAGENCYF AS AGCY_DEAGENCYF, agcy.DTTXNUPDATE AS AGCY_DTTXNUPDATE,\n" +
         // Work location

        "loc.CDLOCAT AS LOC_CDLOCAT, loc.CDLOCTYPE AS LOC_CDLOCTYPE,\n" +
                "loc.DELOCAT AS LOC_DELOCAT,\n" +
        "loc.FFADSTREET1 AS LOC_FFADSTREET1, loc.FFADSTREET2 AS LOC_FFADSTREET2,\n" +
        "loc.FFADCITY AS LOC_FFADCITY, loc.ADSTATE AS LOC_ADSTATE,\n" +
        "loc.ADZIPCODE AS LOC_ADZIPCODE, loc.DTTXNUPDATE AS LOC_DTTXNUPDATE\n" +

        "FROM ${masterSchema}.PM21PERSONN per\n" +
        "LEFT JOIN ${masterSchema}.PL21EMPTITLE ttl ON per.CDEMPTITLE = ttl.CDEMPTITLE\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.PM21ADDRESS WHERE CDADDRTYPE = 'LEGL') addr ON per.NUXREFEM = addr.NUXREFEM\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.PM21EMPXREF WHERE CDSTATUS = 'A') xref ON per.NUXREFEM = xref.NUXREFEM\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16RESPCTR WHERE CDSTATUS = 'A') rctr\n" +
        "  ON per.CDRESPCTR = rctr.CDRESPCTR AND per.CDAGENCY = rctr.CDAGENCY\n" +
        "    AND SYSDATE BETWEEN rctr.DTEFFECTBEG AND rctr.DTEFFECTEND\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16RSPCTRHD WHERE CDSTATUS = 'A') rctrhd ON rctr.CDRESPCTRHD = rctrhd.CDRESPCTRHD\n" +
        "LEFT JOIN (SELECT * FROM ${masterSchema}.SL16AGENCY WHERE CDSTATUS = 'A') agcy ON rctr.CDAGENCY = agcy.CDAGENCY\n" +
        "LEFT JOIN ${masterSchema}.SL16LOCATION loc ON per.CDLOCAT = loc.CDLOCAT\n"
    ),

    GET_EMP_BY_ID_SQL(
            GET_EMP_SQL_TMPL.getSql() +  "WHERE per.NUXREFEM = :empId"
    ),
    GET_EMP_BY_EMAIL_SQL(
            GET_EMP_SQL_TMPL.getSql() + "WHERE per.NAEMAIL = :email"
    ),
    GET_ACTIVE_EMPS_SQL(
            GET_EMP_SQL_TMPL.getSql() + "WHERE per.CDEMPSTATUS = 'A'"
    ),
    GET_EMPS_BY_IDS_SQL(
            GET_EMP_SQL_TMPL.getSql() + "WHERE per.NUXREFEM IN :empIdSet"
    ),

    GET_ACTIVE_EMP_IDS(
        "SELECT DISTINCT NUXREFEM\n" +
        "FROM ${masterSchema}.PM21PERSONN\n" +
        "WHERE CDEMPSTATUS = 'A'"
    ),

    GET_EMP_BY_UPDATE_DATE(
        GET_EMP_SQL_TMPL.getSql() +
        "WHERE per.DTTXNUPDATE > :lastUpdate OR ttl.DTTXNUPDATE > :lastUpdate OR addr.DTTXNUPDATE > :lastUpdate\n" +
        "   OR xref.DTTXNUPDATE > :lastUpdate OR rctr.DTTXNUPDATE > :lastUpdate OR rctrhd.DTTXNUPDATE > :lastUpdate\n" +
        "   OR loc.DTTXNUPDATE > :lastUpdate"
    ),

    GET_LATEST_UPDATE_DATE(
        "SELECT MAX(DTTXNUPDATE) AS MAX_UPDATE_DATE FROM (\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.PM21PERSONN\n" +
        "   WHERE CDSTATUS = 'A'\n" +
        "   UNION\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.PL21EMPTITLE\n" +
        "   WHERE CDSTATUS = 'A'\n" +
        "   UNION\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.PM21ADDRESS\n" +
        "   WHERE CDSTATUS = 'A' AND CDADDRTYPE = 'LEGL'\n" +
        "   UNION\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.PM21EMPXREF\n" +
        "   WHERE CDSTATUS = 'A'\n" +
        "   UNION\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.SL16RESPCTR\n" +
        "   WHERE CDSTATUS = 'A' AND SYSDATE BETWEEN DTEFFECTBEG AND DTEFFECTEND\n" +
        "   UNION" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.SL16RSPCTRHD\n" +
        "   WHERE CDSTATUS = 'A'\n" +
        "   UNION\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.SL16AGENCY\n" +
        "   WHERE CDSTATUS = 'A'\n" +
        "   UNION\n" +
        "   SELECT MAX(DTTXNUPDATE) AS DTTXNUPDATE FROM ${masterSchema}.SL16LOCATION\n" +
        "   WHERE CDSTATUS = 'A'\n" +
        ")"
    )
    ;

    private String sql;

    SqlEmployeeQuery(String sql) {
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
