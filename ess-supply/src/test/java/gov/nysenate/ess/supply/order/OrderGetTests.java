package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderGetTests extends SupplyTests {

    @Autowired
    private OrderService orderService;

    private Order pending;
    private Order processing;
    private Order completed;
    private Order rejected;
    private LimitOffset limOff;

    @Before
    public void setUp() {
        TestUtils.resetInMemoryDaos();
        pending = TestUtils.submitOrder();
        processing = TestUtils.submitAndProcessOrder();
        completed = TestUtils.submitProcessAndCompleteOrder();
        rejected = createRejectedOrder();
        limOff = LimitOffset.ALL;
    }

    @Test
    public void canGetOrderById() {
        Order order = orderService.getOrderById(TestUtils.submitOrder().getId());
        assertNotNull(order);
    }

    @Test
    public void canGetAllOrders() {
        List<Order> allOrders = createOrderList(pending, processing, completed, rejected);
        assertTrue(getOrders(EnumSet.allOf(OrderStatus.class)).containsAll(allOrders));
    }

    @Test
    public void canGetOnlyPendingOrders() {
        List<Order> pendingOrders = createOrderList(pending);
        List<Order> nonPendingOrders = createOrderList(processing, completed, rejected);
        assertTrue(getOrders(EnumSet.of(OrderStatus.PENDING)).containsAll(pendingOrders));
        assertOrderListDoesNotContain(getOrders(EnumSet.of(OrderStatus.PENDING)), nonPendingOrders);
    }

    @Test
    public void canGetOnlyProcessingOrders() {
        List<Order> processingOrders = createOrderList(processing);
        List<Order> nonProcessingOrders = createOrderList(pending, completed, rejected);
        assertTrue(getOrders(EnumSet.of(OrderStatus.PROCESSING)).containsAll(processingOrders));
        assertOrderListDoesNotContain(getOrders(EnumSet.of(OrderStatus.PROCESSING)), nonProcessingOrders);
    }

    @Test
    public void canGetOnlyCompletedOrders() {
        List<Order> completedOrders = createOrderList(completed);
        List<Order> nonCompletedOrders = createOrderList(pending, processing, rejected);
        assertTrue(getOrders(EnumSet.of(OrderStatus.COMPLETED)).containsAll(completedOrders));
        assertOrderListDoesNotContain(getOrders(EnumSet.of(OrderStatus.COMPLETED)), nonCompletedOrders);
    }

    @Test
    public void canGetOnlyRejectedOrders() {
        List<Order> rejectedOrders = createOrderList(rejected);
        List<Order> nonRejectedOrders = createOrderList(pending, processing, completed);
        assertTrue(getOrders(EnumSet.of(OrderStatus.REJECTED)).containsAll(rejectedOrders));
        assertOrderListDoesNotContain(getOrders(EnumSet.of(OrderStatus.REJECTED)), nonRejectedOrders);
    }

    @Test
    public void canGetPendingAndProcessingOrders() {
        List<Order> pendingOrProcessing = createOrderList(pending, processing);
        List<Order> nonPendingOrProcessing = createOrderList(completed, rejected);
        assertTrue(getOrders(EnumSet.of(OrderStatus.PENDING, OrderStatus.PROCESSING)).containsAll(pendingOrProcessing));
        assertOrderListDoesNotContain(getOrders(EnumSet.of(OrderStatus.PENDING, OrderStatus.PROCESSING)), nonPendingOrProcessing);
    }

    /**
     * @param expected List of orders which should not contain any orders in missingOrders.
     * @param missingOrders List of orders which should all be missing from expected.
     */
    private void assertOrderListDoesNotContain(List<Order> expected, List<Order> missingOrders) {
        for (Order missing : missingOrders) {
            assertTrue(!expected.contains(missing));
        }
    }

    private List<Order> createOrderList(Order... orders) {
        return Arrays.asList(orders);
    }

    private List<Order> getOrders(EnumSet<OrderStatus> statuses) {
        return orderService.getOrders(statuses, TestUtils.getDateRange(), limOff);
    }

    private Order createRejectedOrder() {
        Order rejected = TestUtils.submitOrder();
        return orderService.rejectOrder(rejected.getId());
    }
}
