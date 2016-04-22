package gov.nysenate.ess.supply.order;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

public interface OrderService {

    int submitOrder(OrderVersion orderVersion);

    Order getOrder(int orderId);

    PaginatedList<Order> getOrders(String location, String customerId, EnumSet<OrderStatus> statuses,
                                   Range<LocalDateTime> updatedDateTimeRange, LimitOffset limOff);

    void updateOrder(Order order, OrderVersion version);

    void approveOrder(Order order, Employee modifiedBy);
}
