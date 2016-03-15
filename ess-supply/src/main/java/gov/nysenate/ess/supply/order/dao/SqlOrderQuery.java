package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlOrderQuery implements BasicSqlQuery {

    INSERT_LINE_ITEM(
            "INSERT INTO ${supplySchema}.line_item(order_id, version_id, item_id, quantity) \n" +
            "VALUES(:orderId, :versionId, :itemId, :quantity)"
    ),
    INSERT_ORDER(
            "INSERT INTO ${supplySchema}.order(version_id, customer_id, destination, order_status, \n" +
            "modified_date_time, modified_by_id, note) \n" +
            "VALUES(:versionId, :customerId, :destination, :orderStatus::${supplySchema}.order_status, \n" +
            ":modifiedDateTime, :modifiedById, :note)"
    ),
    GET_ORDER_BY_ID(
            "SELECT order_id, version_id, customer_id, destination, order_status, \n" +
            "note, modified_date_time, modified_by_id \n" +
            "FROM ${supplySchema}.order \n" +
            "WHERE order_id = :orderId"
    );

    SqlOrderQuery(String sql) {
        this.sql = sql;
    }

    private String sql;

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
