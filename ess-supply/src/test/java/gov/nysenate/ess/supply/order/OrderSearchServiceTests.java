package gov.nysenate.ess.supply.order;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.order.service.OrderSearchService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class OrderSearchServiceTests extends SupplyTests {

    @Autowired private OrderService orderService;
    @Autowired private OrderSearchService searchService;

    private static final Range<LocalDateTime> ONE_WEEK_RANGE = Range.closed(LocalDateTime.now().minusWeeks(1).plusMinutes(5),
                                                                            LocalDateTime.now().plusMinutes(5));

    @Test
    public void canGetOrderById() {
        Order expected = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        Order actual = searchService.getOrderById(expected.getId());
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void canGetOrdersByLocation() {
        Order expected = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        PaginatedList<Order> results = searchService.getOrders(TestUtils.CUSTOMER_LOC_CODE, TestUtils.CUSTOMER_LOC_TYPE, "all",
                                               EnumSet.allOf(OrderStatus.class), ONE_WEEK_RANGE, LimitOffset.ALL);
        assertThat(results.getResults().get(0), equalTo(expected));
    }

    @Test
    public void canGetOrdersByIssuer() {
        Order expected = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        expected = orderService.processOrder(expected.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        PaginatedList<Order> results = searchService.getOrders("all", "all", String.valueOf(TestUtils.ISSUING_EMP_ID),
                                                               EnumSet.allOf(OrderStatus.class), ONE_WEEK_RANGE, LimitOffset.ALL);
        assertThat(results.getResults().get(0), equalTo(expected));
    }

    @Ignore
    @Test
    public void canGetOrdersByStatus() {
        Order order = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        PaginatedList<Order> processingOrders = searchService.getOrders(EnumSet.of(OrderStatus.PROCESSING), ONE_WEEK_RANGE, LimitOffset.ALL);
        assertThat(processingOrders.getTotal(), is(0));

        Order expected = orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        processingOrders = searchService.getOrders(EnumSet.of(OrderStatus.PROCESSING), ONE_WEEK_RANGE, LimitOffset.ALL);
        assertThat(processingOrders.getTotal(), is(1));
        assertThat(processingOrders.getResults().get(0), equalTo(expected));
    }

    @Test
    public void canGetOrderByDateRange() {
        Order outOfRange = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        LocalDateTime from = LocalDateTime.now();
        Order inRange = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        LocalDateTime to = LocalDateTime.now();

        PaginatedList<Order> orders = searchService.getOrders(EnumSet.allOf(OrderStatus.class), Range.closed(from, to), LimitOffset.ALL);
        assertThat(orders.getTotal(), is(1));
        assertThat(orders.getResults().get(0), equalTo(inRange));
    }

    @Test
    public void canGetOrdersByLimitOffset() {
        Order firstOrder = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        Order secondOrder = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);

        PaginatedList<Order> orders = searchService.getOrders(EnumSet.allOf(OrderStatus.class), ONE_WEEK_RANGE, LimitOffset.ONE);
        assertThat(orders.getTotal(), is(2));
        assertThat(orders.getResults().size(), is(1));
        assertThat(orders.getResults().get(0), equalTo(firstOrder));
    }

//    @Test
//    public void paginatedListResultContainsCorrectInfo() {
//    }
}
