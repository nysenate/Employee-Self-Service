package gov.nysenate.ess.supply.shipment.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.*;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderService;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    // TODO: optimistic locking
    @Override
    public void save(Shipment shipment) {
        // should we check if curr version id = 0?
        int versionId = shipmentVersionDao.insertVersion(shipment.current());
        shipmentHistoryDao.insertHistory(shipment.getId(), versionId, shipment.getModifiedDateTime());
        updateShpment(shipment, versionId);
    }

    private void updateShpment(Shipment shipment, int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("shipmentId", shipment.getId())
                .addValue("versionId", versionId);
        String sql = SqlShipmentQuery.UPDATE_ACTIVE_VERSION.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    @Override
    public Shipment getById(int shipmentId) {
        ShipmentHistory history = shipmentHistoryDao.getHistoryByShipmentId(shipmentId);
        Order order = orderService.getOrder(getShipmentOrderId(shipmentId));
        return Shipment.of(shipmentId, order, history);
    }

    @Override
    public synchronized PaginatedList<Shipment> searchShipments(String issuingEmpId, EnumSet<ShipmentStatus> statuses,
                                                   Range<LocalDateTime> dateRange, LimitOffset limoff) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("issueEmpId", formatSearchString(issuingEmpId))
                .addValue("statuses", extractEnumSetParams(statuses))
                .addValue("startDate", toDate(DateUtils.startOfDateTimeRange(dateRange)))
                .addValue("endDate", toDate(DateUtils.endOfDateTimeRange(dateRange)));
        String sql = SqlShipmentQuery.SEARCH_SHIPMENTS.getSql(schemaMap(), limoff);
        PaginatedRowHandler<Shipment> handler = new PaginatedRowHandler<>(limoff, "total_rows", new ShipmentRowMapper(orderService, shipmentHistoryDao));
        localNamedJdbc.query(sql, params, handler);
        return handler.getList();
    }

    private int getShipmentOrderId(int shipmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource("shipmentId", shipmentId);
        String sql = SqlShipmentQuery.GET_SHIPMENT_ORDER_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, (rs, i) -> {
            return rs.getInt("order_id");
        });
    }

    private String formatSearchString(String param) {
        return param != null && param.equals("all") ? "%" : param;
    }

    /** Convert an EnumSet into a Set containing each enum's name. */
    private Set<String> extractEnumSetParams(EnumSet<ShipmentStatus> statuses) {
        return statuses.stream().map(Enum::name).collect(Collectors.toSet());
    }

    private enum SqlShipmentQuery implements BasicSqlQuery {

        INSERT_SHIPMENT(
                "INSERT INTO ${supplySchema}.shipment(active_version_id, order_id) \n" +
                "VALUES (:activeVersion, :orderId)"
        ),
        GET_SHIPMENT_ORDER_ID(
                "SELECT order_id FROM ${supplySchema}.shipment WHERE shipment_id = :shipmentId"
        ),
        UPDATE_ACTIVE_VERSION(
                "UPDATE ${supplySchema}.shipment SET active_version_id = :versionId \n" +
                "WHERE shipment_id = :shipmentId"
        ),
        SEARCH_SHIPMENTS_BODY(
                "FROM ${supplySchema}.shipment as s \n" +
                "INNER JOIN ${supplySchema}.shipment_history as h \n" +
                "ON (s.shipment_id, s.active_version_id) = (h.shipment_id, h.version_id) \n" +
                "INNER JOIN ${supplySchema}.shipment_version as v \n" +
                "ON s.active_version_id = v.version_id \n" +
                "WHERE Coalesce(v.issuing_emp_id::text, '') LIKE :issueEmpId \n" +
                "AND v.status::text IN (:statuses) AND h.created_date_time BETWEEN :startDate AND :endDate"
        ),
        SEARCH_SHIPMENTS_TOTAL(
                "SELECT count(s.shipment_id) " + SEARCH_SHIPMENTS_BODY.getSql()
        ),
        SEARCH_SHIPMENTS(
                "SELECT s.shipment_id, s.order_id, (" + SEARCH_SHIPMENTS_TOTAL.getSql() + ") as total_rows \n" + SEARCH_SHIPMENTS_BODY.getSql()
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

    private class ShipmentRowMapper extends BaseRowMapper<Shipment> {

        private OrderService orderService;
        private SqlShipmentHistoryDao historyDao;

        public ShipmentRowMapper(OrderService orderService, SqlShipmentHistoryDao historyDao) {
            this.orderService = orderService;
            this.historyDao = historyDao;
        }

        @Override
        public Shipment mapRow(ResultSet rs, int i) throws SQLException {
            Order order = orderService.getOrder(rs.getInt("order_id"));
            int shipmentId = rs.getInt("shipment_id");
            ShipmentHistory history = historyDao.getHistoryByShipmentId(shipmentId);
            return Shipment.of(shipmentId, order, history);
        }
    }
}
