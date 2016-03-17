package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlOrderQuery implements BasicSqlQuery {

    INSERT_ORDER(
            "INSERT INTO ${supplySchema}.order(active_version) VALUES (:activeVersion)"
    ),
    UPDATE_ORDER(
            "UPDATE ${supplySchema}.order \n" +
            "SET active_version = :activeVersion \n" +
            "WHERE order_id = :orderId"
    ),
    ORDER_SEARCH_BODY(
            "FROM ${supplySchema}.order as o \n" +
            "INNER JOIN ${supplySchema}.order_history as h \n" +
            "ON (o.order_id, o.active_version) = (h.order_id, h.version_id) \n" +
            "INNER JOIN ${supplySchema}.order_version as v \n" +
            "ON o.active_version = v.version_id \n" +
            "WHERE v.destination LIKE :location AND Coalesce(v.customer_id::text, '') LIKE :customerId \n" +
            "AND v.status::text In (:statuses) AND h.created_date_time Between :startDate And :endDate"
    ),
    ORDER_TOTAL(
            "SELECT count(o.order_id) " + ORDER_SEARCH_BODY.getSql()
    ),
    ORDER_SEARCH(
            "SELECT o.order_id, (" + ORDER_TOTAL.getSql() + ") as total_rows " + ORDER_SEARCH_BODY.getSql()
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
