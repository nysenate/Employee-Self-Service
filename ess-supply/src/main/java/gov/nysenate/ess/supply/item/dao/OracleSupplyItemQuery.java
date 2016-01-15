package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum OracleSupplyItemQuery implements BasicSqlQuery {

    GET_ALL_SUPPLY_ITEMS(
            "SELECT com.NUXREFCO, xref.CDCOMMODITY, com.CDCATEGORY, com.CDISSUNIT, com.DECOMMODITYF\n" +
            "FROM ${masterSchema}.FM12COMMODTY com INNER JOIN ${masterSchema}.FM12COMXREF xref ON com.NUXREFCO = xref.NUXREFCO\n" +
            "WHERE com.CDSTATUS = 'A' AND com.CDSTOCKITEM = 'Y'"
    ),
    GET_SUPPLY_ITEM_BY_ID(
            "SELECT com.NUXREFCO, xref.CDCOMMODITY, com.CDCATEGORY, com.CDISSUNIT, com.DECOMMODITYF\n" +
            "FROM ${masterSchema}.FM12COMMODTY com INNER JOIN ${masterSchema}.FM12COMXREF xref ON com.NUXREFCO = xref.NUXREFCO\n" +
            "WHERE com.CDSTATUS = 'A' AND com.CDSTOCKITEM = 'Y' AND com.NUXREFCO = :id"
    );

    private String sql;

    OracleSupplyItemQuery(String sql) {
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
