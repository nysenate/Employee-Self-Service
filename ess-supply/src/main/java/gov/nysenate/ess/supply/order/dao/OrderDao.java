package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public interface OrderDao {

    int insertOrder(OrderVersion version, LocalDateTime modifyDateTime);

    void saveOrder(Order order);

    Order getOrderById(int orderId);

    PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                   Range<LocalDateTime> dateTimeRange, LimitOffset limOff);

    Set<Order> getOrderHistory(int orderId);
}
