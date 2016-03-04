package gov.nysenate.ess.supply.order.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.dao.SfmsOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
public class SqlOrderSearchService implements OrderSearchService {

    @Autowired private OrderDao orderDao;

    @Autowired private SfmsOrderDao sfmsDao;

    @Override
    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public PaginatedList<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return getOrders("all", "all", "all", statuses, dateTimeRange, limOff);
    }

    @Override
    public PaginatedList<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                 Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return orderDao.getOrders(locCode, locType, issuerEmpId, statuses, dateTimeRange, limOff);
    }

    @Override
    public PaginatedList<SfmsOrder> getSfmsOrders(Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return getSfmsOrders("all", "all", "all", dateTimeRange, limOff);
    }

    @Override
    public PaginatedList<SfmsOrder> getSfmsOrders(String locCode, String locType, String issueEmpName,
                                                  Range<LocalDateTime> dateTimeRange, LimitOffset limOff) {
        return sfmsDao.getOrders(locCode, locType, issueEmpName, dateTimeRange, limOff);
    }

    @Override
    public Set<Order> getOrderHistory(int order_id) {
        return null;
    }
}
