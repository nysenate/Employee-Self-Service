package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.Assert.*;

public class OrderTests extends SupplyTests {

    @Autowired
    private OrderService orderService;

    @Qualifier("sfmsInMemoryOrder")
    @Autowired
    private OrderDao sfmsDao;

    @Before
    public void setUp() {
        TestUtils.resetInMemoryDaos();
    }

    @Test
    public void newOrderShouldBeGivenId() {
        Order order = submitOrder();
        assertTrue(order.getId() > 0);
    }

    @Test
    public void newOrderDateTimeShouldBeInitialized() {
        Order order = submitOrder();
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
    }

    @Test
    public void newOrderShouldHavePendingStatus() {
        Order order = submitOrder();
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canEditOrderItems() {
        Order order = submitOrder();
        Set<LineItem> originalItems = order.getItems();
        Set<LineItem> newItems = incrementItemQuantities(originalItems);
        order = orderService.updateOrderItems(order.getId(), newItems);
        assertEquals(order.getItems(), newItems);
    }

    @Test
    public void processingOrderShouldUpdateStatus() {
        Order order = submitAndProcessOrder();
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
    }

    @Test
    public void processingOrderSetsIssuingEmployee() {
        Order order = submitOrder();
        Employee issuingEmployee = TestUtils.createEmployee();
        order = orderService.processOrder(order.getId(), issuingEmployee);
        assertEquals(order.getIssuingEmployee(), issuingEmployee);
    }

    @Test
    public void processingOrderSetsProcessedDateTime() {
        Order order = submitAndProcessOrder();
        assertDateLessThan5SecondsOld(order.getProcessedDateTime());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        Order order = submitAndProcessOrder();
        orderService.processOrder(order.getId(), new Employee());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        Order order = submitProcessAndCompleteOrder();
        orderService.processOrder(order.getId(), new Employee());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        Order order = submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.processOrder(order.getId(), new Employee());
    }

    @Test
    public void completingOrderShouldUpdateStatus() {
        Order order = submitProcessAndCompleteOrder();
        assertEquals(order.getStatus(), OrderStatus.COMPLETED);
    }

    @Test
    public void completingOrderSetsCompletedDateTime() {
        Order order = submitProcessAndCompleteOrder();
        assertDateLessThan5SecondsOld(order.getCompletedDateTime());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        Order order = submitOrder();
        orderService.completeOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        Order order = submitProcessAndCompleteOrder();
        orderService.completeOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        Order order = submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.completeOrder(order.getId());
    }

    @Test
    public void completingOrderUpdatesSfms() {
        Order order = submitAndProcessOrder();
        assertTrue(sfmsDao.getOrders().size() == 0);
        orderService.completeOrder(order.getId());
        assertTrue(sfmsDao.getOrders().size() == 1);
    }

    @Test
    public void canGetOrderById() {
        Order order = orderService.getOrderById(submitOrder().getId());
        assertNotNull(order);
    }

    @Test
    public void canGetAllOrders() {
        assertTrue(orderService.getOrders().size() == 0);
        Order order = submitOrder();
        assertTrue(orderService.getOrders().contains(order));
    }

    @Test
    public void canGetPendingOrders() {
        Order order = submitOrder();
        List<Order> pendingOrders = orderService.getPendingOrders();
        assertTrue(pendingOrders.contains(order));
    }
    @Test
    public void canGetOrdersInProcessing() {
        Order order = submitAndProcessOrder();
        List<Order> processingOrders = orderService.getProcessingOrders();
        assertTrue(processingOrders.contains(order));
    }

    @Test
    public void canGetCompletedOrders() {
        submitProcessAndCompleteOrder();
        assertTrue(orderService.getCompletedOrders().size() > 0);
    }

    @Test
    public void canGetCompletedOrdersInDateRange() {
        Order outOfRange = submitProcessAndCompleteOrder();
        sleep(10);
        LocalDateTime start = LocalDateTime.now();
        Order inRange = submitProcessAndCompleteOrder();
        LocalDateTime end = LocalDateTime.now();
        assertTrue(orderService.getCompletedOrdersBetween(start, end).contains(inRange));
        assertFalse(orderService.getCompletedOrdersBetween(start, end).contains(outOfRange));
    }

    @Test
    public void canRejectOrder() {
        Order order = submitOrder();
        assertEquals(order.getStatus(), OrderStatus.PENDING);
        order = orderService.rejectOrder(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantRejectProcessingOrder() {
        Order order = submitAndProcessOrder();
        orderService.rejectOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantRejectCompletedOrder() {
        Order order = submitProcessAndCompleteOrder();
        orderService.rejectOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantRejectRejectedOrder() {
        Order order = submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.rejectOrder(order.getId());
    }

    private Order submitOrder() {
        return orderService.submitOrder(TestUtils.createEmployee(), TestUtils.createLocation(), TestUtils.orderedItemsToQuantitiesMap());
    }

    private Order submitAndProcessOrder() {
        Order order = submitOrder();
        return orderService.processOrder(order.getId(), new Employee());
    }

    private Order submitProcessAndCompleteOrder() {
        Order order = submitAndProcessOrder();
        return orderService.completeOrder(order.getId());
    }

    private Set<LineItem> incrementItemQuantities(Set<LineItem> originalItems) {
        Set<LineItem> newItems = new HashSet<>();
        for (LineItem lineItem : originalItems) {
            newItems.add(new LineItem(lineItem.getItemId(), lineItem.getQuantity() + 1));
        }
        return newItems;
    }

    private void assertDateLessThan5SecondsOld(LocalDateTime orderDateTime) {
        assertTrue(orderDateTime.toInstant(ZoneOffset.UTC).isAfter(LocalDateTime.now().minusSeconds(5).toInstant(ZoneOffset.UTC)));
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
