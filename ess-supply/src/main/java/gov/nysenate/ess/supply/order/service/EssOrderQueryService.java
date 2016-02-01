package gov.nysenate.ess.supply.order.service;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.dao.SfmsOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
public class EssOrderQueryService implements OrderQueryService {

    @Autowired private OrderDao orderDao;

    @Autowired private SfmsOrderDao sfmsDao;

    @Override
    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public List<Order> getOrders(EnumSet<OrderStatus> statuses, Range<LocalDate> dateRange, LimitOffset limOff) {
        return getOrders("all", "all", "all", statuses, dateRange, limOff);
    }

    @Override
    public List<Order> getOrders(String locCode, String locType, String issuerEmpId, EnumSet<OrderStatus> statuses,
                                 Range<LocalDate> dateRange, LimitOffset limOff) {
        return orderDao.getOrders(locCode, locType, issuerEmpId, statuses, dateRange, limOff);
    }

    @Override
    public List<SfmsOrder> getSfmsOrders(Range<LocalDate> dateRange, LimitOffset limOff) {
        return getSfmsOrders("all", "all", "all", dateRange, limOff);
    }

    @Override
    public List<SfmsOrder> getSfmsOrders(String locCode, String locType, String issueEmpName, Range<LocalDate> dateRange, LimitOffset limOff) {
        return sfmsDao.getOrders(locCode, locType, issueEmpName, dateRange, limOff);
    }
}
