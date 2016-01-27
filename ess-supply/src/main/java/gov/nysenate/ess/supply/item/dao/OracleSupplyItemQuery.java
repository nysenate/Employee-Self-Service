package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum OracleSupplyItemQuery implements BasicSqlQuery {

    GET_ALL_SUPPLY_ITEMS(
            "Select com.Nuxrefco, xref.CdCommodity, com.CdCategory, com.CdIssUnit, com.DeCommodityf, unit.AmStdUnit \n" +
            "From ${masterSchema}.FM12COMMODTY com \n" +
            "Inner Join ${masterSchema}.FM12COMXREF xref On com.Nuxrefco = xref.Nuxrefco \n" +
            "Inner Join ${masterSchema}.FL12STDUNIT unit On com.CdIssUnit = unit.CdStdUnit \n" +
            "Where com.CdStatus= 'A' And com.CdStockItem= 'Y'"
    ),
    GET_SUPPLY_ITEM_BY_ID(
            "Select com.Nuxrefco, xref.CdCommodity, com.CdCategory, com.CdIssUnit, com.DeCommodityf, unit.AmStdUnit\n" +
            "From ${masterSchema}.FM12COMMODTY com \n" +
            "Inner Join ${masterSchema}.FM12COMXREF xref On com.Nuxrefco = xref.Nuxrefco \n" +
            "Inner Join ${masterSchema}.FL12STDUNIT unit On com.CdIssUnit = unit.CdStdUnit \n" +
            "Where com.CdStatus = 'A' AND com.CdstockItem = 'Y' AND com.Nuxrefco = :id"
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
