package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlOrderQuery implements BasicSqlQuery {

    INSERT_ORDER_VERSION(
            "INSERT INTO ${supplySchema}.order_version(customer_id, destination, status, note, modified_by) \n" +
            "VALUES (:customerId, :destination, :status::${supplySchema}.order_status, :note, :modifiedById)"
    ),
    INSERT_LINE_ITEM(
            "INSERT INTO ${supplySchema}.line_item(version_id, item_id, quantity) \n" +
            "VALUES (:versionId, :itemId, :quantity)"
    ),
    INSERT_ORDER(
            "INSERT INTO ${supplySchema}.order(active_version) VALUES (:activeVersion)"
    ),
    INSERT_ORDER_HISTORY(
            "INSERT INTO ${supplySchema}.order_history(order_id, version_id, created_date_time) \n" +
            "VALUES (:orderId, :versionId, :createdDateTime)"
    ),
    GET_ORDER_HISTORY(
            "SELECT version_id, created_date_time \n" +
            "FROM ${supplySchema}.order_history \n" +
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
