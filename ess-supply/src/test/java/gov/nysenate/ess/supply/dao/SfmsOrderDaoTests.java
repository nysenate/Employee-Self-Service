package gov.nysenate.ess.supply.dao;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.dao.EssSfmsOrderDao;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TransactionConfiguration(transactionManager = "remoteTxManager", defaultRollback = true)
public class SfmsOrderDaoTests extends SupplyTests {

    @Autowired
    private EssSfmsOrderDao orderDao;

    private static final String SUPPLY_LOC_CODE = "LC100S";
    private static final String SUPPLY_LOC_TYPE = "P";

    @Test
    public void canGetSfmsOrders() {
        List<SfmsOrder> actualOrders = orderDao.getOrders("all", "all", "all", SIX_MONTH_RANGE, LimitOffset.TEN);
        assertThat(actualOrders.size(), greaterThan(0));
        assertOnlySupplyOrdersRetrieved(actualOrders);
    }

    private void assertOnlySupplyOrdersRetrieved(List<SfmsOrder> actualOrders) {
        for (SfmsOrder order: actualOrders) {
            assertThat(order.getFromLocationCode(), is(SUPPLY_LOC_CODE));
            assertThat(order.getFromLocationType(), is(SUPPLY_LOC_TYPE));
        }
    }

    @Transactional
    @Test
    public void canGetSfmsOrdersByLocation() {
        int originalSize = orderDao.getOrders("A42FB", "W", "all", ONE_WEEK_RANGE, LimitOffset.ALL).size();
        Order order = submitProcessAndCompleteOrder();
        orderDao.saveOrder(order);
        int actualSize = orderDao.getOrders("A42FB", "W", "all", ONE_WEEK_RANGE, LimitOffset.ALL).size();
        assertThat(actualSize, is(originalSize + 1));
    }

    @Transactional
    @Test
    public void canGetSfmsOrdersByIssuer() {
        int originalSize = orderDao.getOrders("all", "all", "CASEIRAS", ONE_WEEK_RANGE, LimitOffset.ALL).size();
        Order order = submitProcessAndCompleteOrder();
        orderDao.saveOrder(order);
        int actualSize = orderDao.getOrders("all", "all", "CASEIRAS", ONE_WEEK_RANGE, LimitOffset.ALL).size();
        assertThat(actualSize, is(originalSize + 1));
    }

    @Transactional
    @Test
    public void canSaveSfmsOrder() {
        List<SfmsOrder> originalOrders = orderDao.getOrders("all", "all", "all", ONE_WEEK_RANGE, LimitOffset.ALL);
        Order order = submitProcessAndCompleteOrder();
        orderDao.saveOrder(order);
        List<SfmsOrder> newOrders = orderDao.getOrders("all", "all", "all", ONE_WEEK_RANGE, LimitOffset.ALL);
        assertThat(newOrders.size(), is(originalOrders.size() + 1));
    }
}
