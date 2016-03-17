package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlShipmentQuery implements BasicSqlQuery {

    INSERT_SHIPMENT(
            "INSERT INTO ${supplySchema}.shipment(active_version_id, order_id) \n" +
            "VALUES (:activeVersion, :orderId)"
    );

    SqlShipmentQuery(String sql) {
        this.sql = sql;
    }

    private String sql;

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public DbVendor getVendor() {
        return DbVendor.POSTGRES;
    }
}
