package gov.nysenate.ess.supply.order.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface OrderService {

    Order getOrderById(int orderId);

    List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff);

    /**
     * Get orders, can filter by parameters.
     * For locCode, locType, and issuerEmpId, if the supplies String == null or 'all',
     * the results will not be filtered by that parameter.
     * @param locCode Location Code
     * @param locType Location Type
     * @param issuerEmpId Issuing Employee Id
     * @return
     */
    List<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                    Range<LocalDate> dateRange, LimitOffset limOff);

    List<Order> getSfmsOrders(Range<LocalDate> dateRange, LimitOffset limOff);

    /**
     * Get orders from the SFMS Oracle databse.
     * Can filter results by any of the parameters.
     * @param locCode Location Code
     * @param locType Location Type
     * @param issuerEmpId Issuing Employee Id
     * @param dateRange Date Range
     * @param limOff Limit Offset
     */
    List<Order> getSfmsOrders(String locCode, String locType, String issuerEmpId,
                              Range<LocalDate> dateRange, LimitOffset limOff);

    Order submitOrder(int empId, Set<LineItem> items);

    void saveOrder(Order order);

    Order processOrder(int orderId, int issuingEmpId);

    Order completeOrder(int orderId);

    Order undoCompletion(int id);

    Order rejectOrder(int orderId);
}
