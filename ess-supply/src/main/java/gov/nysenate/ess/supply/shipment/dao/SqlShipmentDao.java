package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class SqlShipmentDao extends SqlBaseDao implements ShipmentDao {

    @Autowired private SqlShipmentVersionDao shipmentVersionDao;
    @Autowired private SqlShipmentHistoryDao shipmentHistoryDao;

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
        return null;
    }
}
