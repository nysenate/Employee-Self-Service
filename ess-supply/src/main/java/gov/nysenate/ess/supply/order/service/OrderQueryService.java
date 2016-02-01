package gov.nysenate.ess.supply.order.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.sfms.SfmsOrder;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

/**
 * Interface responsible for querying and filtering Order's from various data stores.
 */
public interface OrderQueryService {

    /**
     * Get an order by its id.
     */
    Order getOrderById(int orderId);

    /**
     * Get orders by statuses, date range and limit offset.
     * @see #getOrders(String, String, String, EnumSet, Range, LimitOffset)
     * @param statuses
     * @param dateRange
     * @param limOff
     * @return
     */
    List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff);

    /**
     * Get orders, can filter by parameters.
     * For locCode, locType, and issuerEmpId if == null or 'all',
     * the results will not be filtered by that parameter.
     * @param locCode Location Code
     * @param locType Location Type
     * @param issuerEmpId Issuing Employee Id
     * @return
     */
    List<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                          Range<LocalDate> dateRange, LimitOffset limOff);

    /**
     * Get orders from the SFMS database by date range and limit offset.
     * @see #getSfmsOrders(String, String, String, Range, LimitOffset)
     * @param dateRange
     * @param limOff
     * @return
     */
    List<SfmsOrder> getSfmsOrders(Range<LocalDate> dateRange, LimitOffset limOff);

    /**
     * Get orders from the SFMS Oracle databse.
     * Can filter results by any of the parameters.
     * @param locCode Location Code
     * @param locType Location Type
     * @param issueEmpName Issuing employee name
     * @param dateRange Date Range
     * @param limOff Limit Offset
     */
    List<SfmsOrder> getSfmsOrders(String locCode, String locType, String issueEmpName,
                                  Range<LocalDate> dateRange, LimitOffset limOff);
}
