package gov.nysenate.ess.supply.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.UnitTestUtils;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.dao.EssSfmsOrderDao;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SfmsOrderDaoTests extends SupplyTests {

    @Autowired
    private EssSfmsOrderDao orderDao;

    @Test
    public void canGetSfmsOrders() {
        List<SfmsOrder> actualOrders = orderDao.getOrders("all", "all", "all", SIX_MONTH_RANGE, LimitOffset.TWENTY_FIVE);
        assertThat(actualOrders.size(), greaterThan(0));
    }

    @Test
    public void canSaveSfmsOrder() {
        List<SfmsOrder> originalOrders = orderDao.getOrders("all", "all", "all", SIX_MONTH_RANGE, LimitOffset.ALL);
        Order order = UnitTestUtils.submitProcessAndCompleteOrder();
        orderDao.saveOrder(order);
        List<SfmsOrder> newOrders = orderDao.getOrders("all", "all", "all", SIX_MONTH_RANGE, LimitOffset.ALL);
        assertThat(newOrders.size(), greaterThan(originalOrders.size()));
    }
}
