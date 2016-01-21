package gov.nysenate.ess.supply.order.dao.sfms;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;

import java.time.LocalDate;
import java.util.List;

public interface SfmsOrderDao {

    /**
     * Gets an partial id value used for Sfms order inserts.
     * NuIssue is the count of orders to a specific location in a single day.
     * i.e. the second order to a location in a day will have a NuIssue of 2.
     */
    int getNuIssue();

    /**
     * Get orders from SFMS Oracle database.
     * Can filter by Location code, Location type, Issuing Employee Id, Date range and Limit Offset.
     *
     * locCode, locType, issuerEmpId should be set to "all" if you do not want to filter by those parameters.
     */
    List<Order> getOrders(String locCode, String locType, String issuerEmpId,
                          Range<LocalDate> dateRange, LimitOffset limOff);

    void saveOrder(Order order);

    void updateOrder(Order order);
}
