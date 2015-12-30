package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderGetTests extends SupplyTests {

    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() {
        TestUtils.resetInMemoryDaos();
    }

    @Test
    public void canGetOrderById() {
        Order order = orderService.getOrderById(TestUtils.submitOrder().getId());
        assertNotNull(order);
    }

    @Test
    public void canGetAllOrders() {
        assertTrue(orderService.getOrders().size() == 0);
        Order order = TestUtils.submitOrder();
        assertTrue(orderService.getOrders().contains(order));
    }

    @Test
    public void canGetPendingOrders() {
        Order order = TestUtils.submitOrder();
        List<Order> pendingOrders = orderService.getPendingOrders();
        assertTrue(pendingOrders.contains(order));
    }
    @Test
    public void canGetProcessingOrders() {
        Order order = TestUtils.submitAndProcessOrder();
        List<Order> processingOrders = orderService.getProcessingOrders();
        assertTrue(processingOrders.contains(order));
    }

    @Test
    public void canGetCompletedOrders() {
        TestUtils.submitProcessAndCompleteOrder();
        assertTrue(orderService.getCompletedOrders().size() > 0);
    }

    @Test
    public void canGetCompletedOrdersInDateRange() {
        Order outOfRange = TestUtils.submitProcessAndCompleteOrder();
        sleep(10);
        LocalDateTime start = LocalDateTime.now();
        Order inRange = TestUtils.submitProcessAndCompleteOrder();
        LocalDateTime end = LocalDateTime.now();
        assertTrue(orderService.getCompletedOrdersBetween(start, end).contains(inRange));
        assertFalse(orderService.getCompletedOrdersBetween(start, end).contains(outOfRange));
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
