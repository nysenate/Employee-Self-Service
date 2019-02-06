package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlLocationQuery implements BasicSqlQuery {

    LOCATION_COLUMNS(
            "loc.DELOCAT AS LOC_DELOCAT,\n" +
            "loc.CDLOCAT AS LOC_CDLOCAT, loc.CDLOCTYPE AS LOC_CDLOCTYPE,\n" +
            "loc.FFADSTREET1 AS LOC_FFADSTREET1, loc.FFADSTREET2 AS LOC_FFADSTREET2,\n" +
            "loc.FFADCITY AS LOC_FFADCITY, loc.ADSTATE AS LOC_ADSTATE,\n" +
            "loc.ADZIPCODE AS LOC_ADZIPCODE, loc.DTTXNUPDATE AS LOC_DTTXNUPDATE, loc.CDSTATUS AS LOC_CDSTATUS"
    ),
    RCTRHD_COLUMNS(
            "rctrhd.CDRESPCTRHD AS RCTRHD_CDRESPCTRHD, rctrhd.CDSTATUS AS RCTRHD_CDSTATUS, " +
            "rctrhd.CDAFFILIATE AS RCTRHD_CDAFFILIATE, rctrhd.DERESPCTRHDS AS RCTRHD_DERESPCTRHDS, \n" +
            "rctrhd.FFDERESPCTRHDF AS RCTRHD_FFDERESPCTRHDF, rctrhd.DTTXNUPDATE AS RCTRHD_DTTXNUPDATE"
    ),
    GET_LOCATIONS_INCLUDING_INACTIVE(
            "Select " + LOCATION_COLUMNS.getSql() + ", \n" + RCTRHD_COLUMNS.getSql() + " \n" +
                    "From ${masterSchema}.SL16LOCATION loc \n" +
                    "Left Join ${masterSchema}.SL16RSPCTRHD rctrhd On loc.CDRESPCTRHD = rctrhd.CDRESPCTRHD"
    ),
    GET_LOCATIONS(
            GET_LOCATIONS_INCLUDING_INACTIVE.getSql() + " \n" +
            "Where loc.CdStatus = 'A' And rctrhd.CdStatus = 'A'"
    ),
    GET_BY_CODE_AND_TYPE(
            "Select " + LOCATION_COLUMNS.getSql() + ", \n" + RCTRHD_COLUMNS.getSql() + " \n" +
            "From ${masterSchema}.SL16LOCATION loc \n" +
            "Left Join ${masterSchema}.SL16RSPCTRHD rctrhd On loc.CDRESPCTRHD = rctrhd.CDRESPCTRHD \n" +
            "Where loc.CdStatus = 'A' And rctrhd.CdStatus = 'A' and loc.CDLOCAT = :locCode And loc.CDLOCTYPE = :locType"
    ),
    SEARCH_LOCATIONS(
            GET_LOCATIONS.getSql() + " \n" +
            "AND loc.cdlocat like :term"
    ),
    GET_LOCATIONS_BY_RESPONSIBILITY_HEADS(
            GET_LOCATIONS.getSql() + " AND loc.CDRESPCTRHD IN (:rchCodes)"
    )
    ;

    private String sql;

    SqlLocationQuery(String sql) {
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
