package gov.nysenate.ess.core.dao.personnel.rch;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlResponsibilityHeadQuery implements BasicSqlQuery {
    RCH_BY_CODE(
            "SELECT CDRESPCTRHD, CDSTATUS, CDAFFILIATE, DERESPCTRHDS, FFDERESPCTRHDF \n" +
                    "FROM ${masterSchema}.SL16RSPCTRHD \n" +
                    "WHERE CDRESPCTRHD = :code"
    ),
    RCHS_BY_CODES(
            "SELECT CDRESPCTRHD, CDSTATUS, CDAFFILIATE, DERESPCTRHDS, FFDERESPCTRHDF \n" +
                    "FROM ${masterSchema}.SL16RSPCTRHD \n" +
                    "WHERE CDRESPCTRHD IN (:codes)"
    ),
    RCHS_SEARCH("" +
            "SELECT CDRESPCTRHD, CDSTATUS, CDAFFILIATE, DERESPCTRHDS, FFDERESPCTRHDF, COUNT(*) OVER () AS total\n" +
            "FROM ${masterSchema}.SL16RSPCTRHD\n" +
            "WHERE CDSTATUS = 'A'\n" +
            "  AND (\n" +
            "    UPPER(CDRESPCTRHD) LIKE '%' || UPPER(:term) || '%'\n" +
            "    OR UPPER(FFDERESPCTRHDF) LIKE '%' || UPPER(:term) || '%'\n" +
            "  )"
    ),
    ;

    private String sql;

    SqlResponsibilityHeadQuery(String sql) {
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

