package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Repository
public class SqlOrderDao extends SqlBaseDao implements OrderDao {

    @Override
    public int insertOrder(OrderVersion version, LocalDateTime modifyDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("versionId", version.getId())
                .addValue("customerId", version.getCustomer().getEmployeeId())
                .addValue("destination", generateLocationId(version.getDestination()))
                .addValue("orderStatus", version.getStatus().toString())
                .addValue("note", version.getNote().orElse(null))
                .addValue("modifiedDateTime", toDate(modifyDateTime))
                .addValue("modifiedById", version.getModifiedBy().getEmployeeId());

        String sql = SqlOrderQuery.INSERT_ORDER.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        Integer orderId = (Integer) keyHolder.getKeys().get("order_id");
        return orderId;
    }

    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public Order getOrderById(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId);
        String sql = SqlOrderQuery.GET_ORDER_BY_ID.getSql(schemaMap());

    }

    @Override
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses, Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public Set<Order> getOrderHistory(int orderId) {
        return null;
    }

    private String generateLocationId(Location destination) {
        return destination.getCode() + "-" + destination.getType().getCode();
    }
}
