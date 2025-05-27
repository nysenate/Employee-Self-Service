package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum OracleSupplyItemQuery implements BasicSqlQuery {

    SUPPLY_ITEM_QUERY(
            "Select com.Nuxrefco, xref.CdCommodity, com.CdCategory, com.CdStockItem, com.CdIssUnit, com.DeCommodityf, \n"
                    + "com.DeCommdtyEssSupply, com.CdSpecPerMReq, com.CdSpecPerMVisible, unit.AmStdUnit, \n"
                    + "com.numaxunitord, com.numaxunitmon, com.cdsensuppieditem, com.nusupreqformpgno \n"
                    + "From ${masterSchema}.FM12COMMODTY com \n"
                    + "Inner Join ${masterSchema}.FM12COMXREF xref On com.Nuxrefco = xref.Nuxrefco \n"
                    + "Inner Join ${masterSchema}.FL12STDUNIT unit On com.CdIssUnit = unit.CdStdUnit \n"
    ),

    GET_SUPPLY_ITEM_BY_ID(
            SUPPLY_ITEM_QUERY.getSql() +
                    "Where com.Nuxrefco = :id"
    ),

    GET_SUPPLY_ITEMS_BY_IDS(
            SUPPLY_ITEM_QUERY.getSql() +
            "WHERE com.nuxrefco IN (:ids)"
    ),

    GET_ALL_SUPPLY_ITEMS(
            SUPPLY_ITEM_QUERY.getSql() +
                    "Where com.CdStatus= 'A' And com.CdStockItem= 'Y'"
    );

    private final String sql;

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
