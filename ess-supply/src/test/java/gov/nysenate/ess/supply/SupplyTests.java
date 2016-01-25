package gov.nysenate.ess.supply;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.config.SupplyConfig;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.InMemorySupplyItemDao;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.dao.InMemoryOrderDao;
import gov.nysenate.ess.supply.order.service.OrderService;
import gov.nysenate.ess.supply.sfms.dao.InMemorySfmsOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@ContextConfiguration(classes = SupplyConfig.class)
public abstract class SupplyTests extends BaseTests {

    @Autowired private SupplyItemService itemService;
    @Autowired private InMemorySupplyItemDao itemDao;
    @Autowired private OrderService orderService;
    @Autowired private InMemoryOrderDao orderDao;
    @Autowired private InMemorySfmsOrderDao sfmsOrderDao;

    protected static final Range<LocalDate> ONE_WEEK_RANGE = Range.closed(LocalDate.now().minusWeeks(1), LocalDate.now());
    protected static final Range<LocalDate> THREE_MONTH_RANGE = Range.closed(LocalDate.now().minusMonths(3), LocalDate.now());
    protected static final Range<LocalDate> SIX_MONTH_RANGE = Range.closed(LocalDate.now().minusMonths(6), LocalDate.now());

    private static final int CUSTOMER_EMP_ID = 1;
    private static final int ISSUING_EMP_ID = 11168;

    protected void resetInMemoryDaos() {
        itemDao.reset();
        orderDao.reset();
        sfmsOrderDao.reset();
    }

    protected Order submitOrder() {
        return orderService.submitOrder(CUSTOMER_EMP_ID, lineItems());
    }

    protected Order submitAndProcessOrder() {
        Order order = submitOrder();
        return orderService.processOrder(order.getId(), ISSUING_EMP_ID);
    }

    protected Order submitProcessAndCompleteOrder() {
        Order order = submitAndProcessOrder();
        return orderService.completeOrder(order.getId());
    }

    protected Range<LocalDate> getDateRange() {
        return Range.closed(LocalDate.MIN, LocalDate.now());
    }

    private Set<LineItem> lineItems() {
        Set<LineItem> lineItems = new HashSet<>();
        for (SupplyItem item : itemService.getSupplyItems(LimitOffset.TEN)) {
            lineItems.add(new LineItem(item, 1));
        }
        return lineItems;
    }
}
