package gov.nysenate.ess.supply.sfms.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.SfmsOrderId;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * Can filter by Location code, Location type, Issuing Employee Name, Date range and Limit Offset.
     *
     * locCode, locType, issuerEmpId should be set to "all" if you do not want to filter by those parameters.
     * @param issueEmpName Usually the last name of the issuing employee.
     */
    PaginatedList<SfmsOrder> getOrders(String locCode, String locType, String issueEmpName,
                          Range<LocalDateTime> dateTimeRange, LimitOffset limOff);

    SfmsOrder getOrderById(SfmsOrderId orderId);

    // TODO: add modified by/datetime to params
    void saveOrder(Order order);

}
