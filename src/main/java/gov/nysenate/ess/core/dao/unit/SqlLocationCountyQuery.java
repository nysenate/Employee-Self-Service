package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlLocationCountyQuery implements BasicSqlQuery {
    SELECT_WORK_ADDRESS_COUNTY(
            "SELECT county FROM ${essSchema}.work_location_county \n" +
                    "WHERE location_code = :locCode and location_type = :locType"
    )
    ;

    private String sql;

    SqlLocationCountyQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
