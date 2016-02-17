package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.order.dao.SqlOrderDao;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class OrderDaoTests extends SupplyTests {

    @Autowired SqlOrderDao orderDao;

    @Test
    public void canGetOrderById() {
        Order expected = orderDao.insertOrder(submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID));
        Order actual = orderDao.getOrderById(expected.getId());
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void canInsertNewOrder() {
        Order expected = orderDao.insertOrder(submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID));
        Order actual = orderDao.getOrderById(expected.getId());
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void canUpdateOrder() {
        Order original = orderDao.insertOrder(submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID));
        Order expected = original.setStatus(OrderStatus.PROCESSING);
        expected = expected.setLineItems(incrementItemQuantities(expected.getLineItems()));
        orderDao.saveOrder(expected);
        Order actual = orderDao.getOrderById(expected.getId());
        assertThat(actual, is(equalTo(expected)));
    }

    // FIXME: inmemorydb assigns a intetger already in use? so results not valid.
    @Ignore
    @Test
    public void canGetOrders() {
        EnumSet<OrderStatus> statuses = EnumSet.allOf(OrderStatus.class);
        int originalCount = orderDao.getOrders("all", "all", "all", statuses, THREE_MONTH_RANGE, LimitOffset.ALL).getResults().size();
        orderDao.insertOrder(submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID));
        int newCount = orderDao.getOrders("all", "all", "all", statuses, THREE_MONTH_RANGE, LimitOffset.ALL).getResults().size();
        assertThat(newCount, is(greaterThan(originalCount)));
    }
}
