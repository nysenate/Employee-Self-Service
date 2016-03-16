package gov.nysenate.ess.supply.order.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlOrderQuery implements BasicSqlQuery {

    INSERT_ORDER(
            "INSERT INTO ${supplySchema}.order(active_version) VALUES (:activeVersion)"
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
