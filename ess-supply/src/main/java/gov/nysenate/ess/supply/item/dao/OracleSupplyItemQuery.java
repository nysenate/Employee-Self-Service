package gov.nysenate.ess.supply.item.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum OracleSupplyItemQuery implements BasicSqlQuery {

    /** Get Item by id */
    GET_SUPPLY_ITEM_BY_ID(
            "Select com.Nuxrefco, xref.CdCommodity, com.CdCategory, com.CdIssUnit, com.DeCommodityf, \n" +
            "com.CdSpecPerMReq, com.CdSpecPerMVisible, unit.AmStdUnit, com.numaxunitord, com.numaxunitmon \n" +
            "From ${masterSchema}.FM12COMMODTY com \n" +
            "Inner Join ${masterSchema}.FM12COMXREF xref On com.Nuxrefco = xref.Nuxrefco \n" +
            "Inner Join ${masterSchema}.FL12STDUNIT unit On com.CdIssUnit = unit.CdStdUnit \n" +
            "Where com.CdStatus = 'A' AND com.CdstockItem = 'Y' AND com.Nuxrefco = :id"
    ),

    /** Get all Items */
    GET_ALL_ITEMS_BODY(
            "From ${masterSchema}.FM12COMMODTY com \n" +
            "Inner Join ${masterSchema}.FM12COMXREF xref On com.Nuxrefco = xref.Nuxrefco \n" +
            "Inner Join ${masterSchema}.FL12STDUNIT unit On com.CdIssUnit = unit.CdStdUnit \n" +
            "Where com.CdStatus= 'A' And com.CdStockItem= 'Y'"
    ),
    ALL_ITEMS_TOTAL_ROWS(
            "SELECT COUNT(*) " + GET_ALL_ITEMS_BODY.getSql()
    ),
    GET_ALL_SUPPLY_ITEMS(
            "Select com.Nuxrefco, xref.CdCommodity, com.CdCategory, com.CdIssUnit, com.DeCommodityf, \n" +
            "com.CdSpecPerMReq, com.CdSpecPerMVisible, unit.AmStdUnit, com.numaxunitord, com.numaxunitmon, \n" +
            "(" + ALL_ITEMS_TOTAL_ROWS.getSql() + ") as total_rows " + GET_ALL_ITEMS_BODY.getSql()
    ),

    /** Get Items by category */
    GET_ITEMS_BY_CATEGORY_BODY(
            "From ${masterSchema}.FM12COMMODTY com \n" +
            "Inner Join ${masterSchema}.FM12COMXREF xref On com.Nuxrefco = xref.Nuxrefco \n" +
            "Inner Join ${masterSchema}.FL12STDUNIT unit On com.CdIssUnit = unit.CdStdUnit \n" +
            "Where com.CdStatus= 'A' And com.CdStockItem= 'Y' AND com.CdCategory IN (:categories)"
    ),
    ITEMS_BY_CATEGORY_TOTAL_ROWS(
            "SELECT COUNT(*) " + GET_ITEMS_BY_CATEGORY_BODY.getSql()
    ),
    GET_ITEMS_BY_CATEGORIES(
            "Select com.Nuxrefco, xref.CdCommodity, com.CdCategory, com.CdIssUnit, com.DeCommodityf, \n" +
            "com.CdSpecPerMReq, com.CdSpecPerMVisible, unit.AmStdUnit, com.numaxunitord, com.numaxunitmon, \n" +
            "(" + ITEMS_BY_CATEGORY_TOTAL_ROWS.getSql() + ") as total_rows " + GET_ITEMS_BY_CATEGORY_BODY.getSql()
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
