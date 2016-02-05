package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Repository
public class SqlOrderDao extends SqlBaseDao implements OrderDao {

    @Autowired EmployeeInfoService employeeInfoService;
    @Autowired LocationService locationService;
    @Autowired SupplyItemService itemService;

    @Override
    public Order insertOrder(Order order, LocalDateTime modifiedDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", order.getStatus().toString())
                .addValue("customerId", order.getCustomer().getEmployeeId())
                .addValue("locCode", order.getLocationCode())
                .addValue("locType", order.getLocationType())
                .addValue("orderDateTime", toDate(order.getOrderDateTime()))
                .addValue("modifiedDateTime", toDate(modifiedDateTime))
                .addValue("modifiedEmpId", order.getCustomer().getEmployeeId());
        String sql = SqlOrderDaoQuery.INSERT_ORDER.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        order = order.setId((Integer) keyHolder.getKeys().get("order_id"));
        insertLineItems(order, modifiedDateTime);
        return order;
    }

    @Override
    public void insertLineItems(Order order, LocalDateTime modifiedDateTime) {
        List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (LineItem lineItem: order.getLineItems()) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("orderId", order.getId())
                    .addValue("itemId", lineItem.getItem().getId())
                    .addValue("quantity", lineItem.getQuantity())
                    .addValue("modifiedDateTime", toDate(modifiedDateTime))
                    .addValue("modifiedEmpId", order.getCustomer().getEmployeeId());
            batchParams.add(params);
        }
        String sql = SqlOrderDaoQuery.INSERT_LINE_ITEMS.getSql(schemaMap());
        localNamedJdbc.batchUpdate(sql, toArray(batchParams));
    }

    private MapSqlParameterSource[] toArray(List<MapSqlParameterSource> batchParams) {
        return batchParams.toArray(new MapSqlParameterSource[batchParams.size()]);
    }

    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public List<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff) {
        return null;
    }

    @Override
    public Order getOrderById(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("orderId", orderId);
        String sql = SqlOrderDaoQuery.GET_ORDER_BY_ID.getSql(schemaMap());
        SqlOrderHandler handler = new SqlOrderHandler(employeeInfoService, locationService, itemService);
        localNamedJdbc.query(sql, params, handler);
        return handler.getOrders().get(0);
    }

    @Override
    public void undoCompletion(Order order) {

    }
}
