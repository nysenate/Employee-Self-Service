package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderService;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class SqlShipmentDao extends SqlBaseDao implements ShipmentDao {

    @Autowired private SqlShipmentVersionDao shipmentVersionDao;
    @Autowired private SqlShipmentHistoryDao shipmentHistoryDao;
    @Autowired private OrderService orderService;

    @Override
    public int insert(Order order, ShipmentVersion version, LocalDateTime modifiedDateTime) {
        int versionId = shipmentVersionDao.insertVersion(version);
        int shipmentId = insertNewShipment(versionId, order.getId());
        shipmentHistoryDao.insertHistory(shipmentId, versionId, modifiedDateTime);
        return shipmentId;
    }

    private int insertNewShipment(int versionId, int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("activeVersion", versionId)
                .addValue("orderId", orderId);
        String sql = SqlShipmentQuery.INSERT_SHIPMENT.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("shipment_id");
    }

    @Override
    public void save(Shipment processed) {

    }

    @Override
    public Shipment getById(int shipmentId) {
        ShipmentHistory history = shipmentHistoryDao.getHistoryByShipmentId(shipmentId);
        Order order = orderService.getOrder(getShipmentOrderId(shipmentId));
        return Shipment.of(shipmentId, order, history);
    }

    private int getShipmentOrderId(int shipmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("shipmentId", shipmentId);
        String sql = SqlShipmentQuery.GET_SHIPMENT_ORDER_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, (rs, i) -> {
            return rs.getInt("order_id");
        });
    }

    private enum SqlShipmentQuery implements BasicSqlQuery {

        INSERT_SHIPMENT(
                "INSERT INTO ${supplySchema}.shipment(active_version_id, order_id) \n" +
                "VALUES (:activeVersion, :orderId)"
        ),
        GET_SHIPMENT_ORDER_ID(
                "SELECT order_id FROM ${supplySchema}.shipment WHERE shipment_id = :shipmentId"
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
}
