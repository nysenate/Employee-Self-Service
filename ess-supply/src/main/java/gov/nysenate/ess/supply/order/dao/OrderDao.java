package gov.nysenate.ess.supply.order.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface OrderDao {

    /**
     * Inserts an order into the local database and sets the order's id.
     * @param order An order to be inserted. Should not have an id value.
     * @return A copy of the passed in order, its id field is now set.
     */
    Order insertOrder(Order order);

    void saveOrder(Order order);

    /**
     * Get orders by location code, location type, issuer, statuses, and date range.
     * @param locCode If 'all', search by all location codes.
     * @param locType If 'all', search by all location types.
     * @param issuerEmpId If 'all', search by all issuer id's.
     * @param statuses
     * @param dateTimeRange
     * @param limOff
     * @return
     */
    PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                             Range<LocalDateTime> dateTimeRange, LimitOffset limOff);

    Order getOrderById(int orderId);

    void undoCompletion(Order order);
}
