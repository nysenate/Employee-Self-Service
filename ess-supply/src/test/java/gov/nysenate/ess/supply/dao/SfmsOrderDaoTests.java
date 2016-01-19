package gov.nysenate.ess.supply.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SfmsOrderDaoTests extends SupplyTests {

    @Autowired
    private OrderService orderService;

    private Range<LocalDate> oneWeekRange = Range.closed(LocalDate.now().minusWeeks(1), LocalDate.now());

    @Before
    public void setUp() {
    }

    // TODO: add custom data to database and add more checks in tests.

    @Ignore
    @Test
    public void canGetOrdersByDateRange() {
        List<Order> actualOrders = orderService.getSfmsOrders(oneWeekRange, LimitOffset.TWENTY_FIVE);
        assertTrue(actualOrders.size() > 0);
    }

    @Ignore
    @Test
    public void canGetOnlyPendingOrders() {
    }

    @Ignore
    @Test
    public void canGetOnlyProcessingOrders() {
    }

    @Ignore
    @Test
    public void canGetOnlyCompletedOrders() {
    }

    @Ignore
    @Test
    public void canGetOnlyRejectedOrders() {
    }

    @Ignore
    @Test
    public void canGetPendingAndProcessingOrders() {
    }
}
