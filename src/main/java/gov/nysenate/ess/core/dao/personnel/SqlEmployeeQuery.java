package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlEmployeeQuery implements BasicSqlQuery
{
    GET_EMP_SQL_COLS(
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
        // Work locationId
        "loc.CDLOCAT AS LOC_CDLOCAT, loc.CDLOCTYPE AS LOC_CDLOCTYPE,\n" +
        "loc.DTTXNUPDATE AS LOC_DTTXNUPDATE\n"
    ),
    GET_EMP_SQL_TABLES(
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
    GET_ALL_EMPS_SQL(
            GET_EMP_SQL_COLS.getSql() + GET_EMP_SQL_TABLES.getSql()
    ),

    GET_EMP_BY_ID_SQL(
            GET_ALL_EMPS_SQL.getSql() +  "WHERE per.NUXREFEM = :empId"
    ),
    GET_ACTIVE_EMP_BY_EMAIL_SQL(
            GET_ALL_EMPS_SQL.getSql() + "WHERE per.NAEMAIL = :email and per.CDEMPSTATUS = 'A'"
    ),
    GET_ACTIVE_EMPS_SQL(
            GET_ALL_EMPS_SQL.getSql() + "WHERE per.CDEMPSTATUS = 'A'"
    ),
    GET_EMPS_BY_IDS_SQL(
            GET_ALL_EMPS_SQL.getSql() + "WHERE per.NUXREFEM IN :empIdSet"
    ),
    GET_EMPS_BY_SEARCH_TERM(
            GET_EMP_SQL_COLS.getSql() + ", COUNT(*) OVER () AS total_rows\n" +
            GET_EMP_SQL_TABLES.getSql() +
            "WHERE UPPER(TRIM(per.NAFIRST) || ' ' || TRIM(per.FFNAMIDINIT) || ' ' || TRIM(per.FFNALAST))\n" +
            "        LIKE UPPER('%' || :term || '%')\n" +
            "  AND (:activeOnly = 0 OR per.CDEMPSTATUS = 'A')"
    ),
    GET_EMPS_BY_SEARCH_QUERY("" +
            GET_EMP_SQL_COLS.getSql() + ", COUNT(*) OVER () AS total_rows\n" +
            GET_EMP_SQL_TABLES.getSql() +
            "WHERE (:empStatus IS NULL OR per.CDEMPSTATUS = :empStatus)\n" +
            "  AND (:name IS NULL OR \n" +
            "    REGEXP_REPLACE(\n" +
            "      UPPER(TRIM(per.FFNALAST) || ' ' || TRIM(per.FFNAFIRST) || ' ' || TRIM(per.FFNAMIDINIT)),\n" +
            "      '[^A-Z ]', ''\n" +
            "    )\n" +
            "      LIKE UPPER(:name || '%')\n" +
            "  )\n" +
            "  AND (:respCtrHeadCodesEmpty = 1 OR rctrhd.CDRESPCTRHD IN (:respCtrHeadCodes))\n" +
            "  AND (:contServFrom IS NULL OR DTCONTSERV >= :contServFrom)\n" +
            "  AND (:contServTo IS NULL OR DTCONTSERV <= :contServTo)"
    ),

    GET_ACTIVE_EMP_IDS(
        "SELECT DISTINCT NUXREFEM\n" +
        "FROM ${masterSchema}.PM21PERSONN\n" +
        "WHERE CDEMPSTATUS = 'A'"
    ),

    GET_EMP_BY_UPDATE_DATE(
        GET_ALL_EMPS_SQL.getSql() +
        "WHERE per.DTTXNUPDATE > :lastUpdate OR ttl.DTTXNUPDATE > :lastUpdate OR addr.DTTXNUPDATE > :lastUpdate\n" +
        "   OR xref.DTTXNUPDATE > :lastUpdate OR rctr.DTTXNUPDATE > :lastUpdate OR rctrhd.DTTXNUPDATE > :lastUpdate\n" +
        "   OR loc.DTTXNUPDATE > :lastUpdate OR agcy.DTTXNUPDATE > :lastUpdate"
    ),

    GET_LATEST_UPDATE_DATE(
        "SELECT GREATEST(\n" +
        "    MAX(per.DTTXNUPDATE),\n" +
        "    MAX(ttl.DTTXNUPDATE),\n" +
        "    MAX(addr.DTTXNUPDATE),\n" +
        "    MAX(xref.DTTXNUPDATE),\n" +
        "    MAX(rctr.DTTXNUPDATE),\n" +
        "    MAX(rctrhd.DTTXNUPDATE),\n" +
        "    MAX(loc.DTTXNUPDATE),\n" +
        "    MAX(agcy.DTTXNUPDATE)\n" +
        ") AS MAX_UPDATE_DATE\n" +
        GET_EMP_SQL_TABLES.getSql()
    ),

    GET_NEW_EMPLOYEES(
            "SELECT *\n" +
                    "FROM  ${masterSchema}.pm21personn\n" +
                    "WHERE cdempstatus = 'A'\n" +
                    "  AND nuxrefem IN (SELECT nuxrefem\n" +
                    "                   FROM  ${masterSchema}.pd21ptxncode\n" +
                    "                   WHERE cdtrans IN ('APP', 'RTP')\n" +
                    "                     AND CASE WHEN dteffect >= TRUNC(dttxnorigin) THEN dteffect\n" +
                    "                              ELSE TRUNC(dttxnorigin)\n" +
                    "                             END >= TRUNC(SYSDATE) - 30\n" +
                    "                     AND cdstatus = 'A')"
    ),

    GET_INACTIVE_EMPLOYEES_SINCE_DATE(
            "SELECT * FROM ${masterSchema}.PM21PERSONN p WHERE p.CDEMPSTATUS = 'I' AND TRUNC(p.DTTXNUPDATE) >= TO_DATE(:since, 'DD-MON-RRRR')"
    );

    private final String sql;

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
