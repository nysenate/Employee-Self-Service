package gov.nysenate.ess.supply.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.dao.sfms.EssSfmsOrderDao;
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
    private EssSfmsOrderDao orderDao;

    @Before
    public void setUp() {
    }

    // TODO: add custom data to database and add more checks in tests.

    @Ignore
    @Test
    public void canGetOrdersByDateRange() {
//        List<Order> actualOrders = orderDao.getOrders(ONE_WEEK_RANGE, LimitOffset.TWENTY_FIVE);
//        assertTrue(actualOrders.size() > 0);
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
