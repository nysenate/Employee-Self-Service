package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class OrderTests extends SupplyTests {

    @Autowired
    private OrderService orderService;

    @Qualifier("sfmsInMemoryOrder")
    @Autowired
    private OrderDao sfmsDao;

    @Before
    public void before() {
        TestUtils.resetInMemoryDaos();
    }

    @Test
    public void newOrderShouldBeGivenId() {
        int orderId = TestUtils.submitOrder();
        assertTrue(orderId > 0);
    }

    @Test
    public void newOrderDateTimeShouldBeInitialized() {
        int orderId = TestUtils.submitOrder();
        Order order = orderService.getOrderById(orderId);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
    }

    @Test
    public void newOrderShouldHavePendingStatus() {
        int orderId = TestUtils.submitOrder();
        Order order = orderService.getOrderById(orderId);
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canEditOrderItems() {
        int orderId = TestUtils.submitOrder();
        Map<Integer, Integer> originalItems = orderService.getOrderById(orderId).getItems();
        Map<Integer, Integer> newItems = incrementItemQuantities(originalItems);
        orderService.updateOrderItems(orderId, newItems);
        assertEquals(orderService.getOrderById(orderId).getItems(), newItems);
    }

    @Test
    public void processingOrderShouldUpdateStatus() {
        int orderId = TestUtils.submitOrder();
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.PENDING);
        orderService.processOrder(orderId, new Employee());
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.PROCESSING);
    }

    @Test
    public void processingOrderSetsIssuingEmployee() {
        int orderId = TestUtils.submitOrder();
        assertNull(orderService.getOrderById(orderId).getIssuingEmployee());
        Employee issuingEmployee = TestUtils.createEmployee();
        orderService.processOrder(orderId, issuingEmployee);
        assertEquals(orderService.getOrderById(orderId).getIssuingEmployee(), issuingEmployee);
    }

    @Test
    public void processingOrderSetsProcessedDateTime() {
        int orderId = TestUtils.submitOrder();
        assertNull(orderService.getOrderById(orderId).getProcessedDateTime());
        orderService.processOrder(orderId, new Employee());
        assertDateLessThan5SecondsOld(orderService.getOrderById(orderId).getProcessedDateTime());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.processOrder(orderId, new Employee());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.completeOrder(orderId);
        orderService.processOrder(orderId, new Employee());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.rejectOrder(orderId);
        orderService.processOrder(orderId, new Employee());
    }

    @Test
    public void completingOrderShouldUpdateStatus() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.completeOrder(orderId);
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.COMPLETED);
    }

    @Test
    public void completingOrderSetsCompletedDateTime() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.completeOrder(orderId);
        assertDateLessThan5SecondsOld(orderService.getOrderById(orderId).getCompletedDateTime());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.completeOrder(orderId);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.completeOrder(orderId);
        orderService.completeOrder(orderId);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.rejectOrder(orderId);
        orderService.completeOrder(orderId);
    }

    @Test
    public void completingOrderUpdatesSfms() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        assertTrue(sfmsDao.getOrders().size() == 0);
        orderService.completeOrder(orderId);
        assertTrue(sfmsDao.getOrders().size() == 1);
    }

    @Test
    public void canGetOrderById() {
        int orderId = TestUtils.submitOrder();
        Order order = orderService.getOrderById(orderId);
        assertNotNull(order);
    }

    @Test
    public void canGetAllOrders() {
        assertTrue(orderService.getOrders().size() == 0);
        int orderId = TestUtils.submitOrder();
        assertTrue(orderService.getOrders().contains(orderService.getOrderById(orderId)));
    }

    @Test
    public void canGetPendingOrders() {
        int orderId = TestUtils.submitOrder();
        List<Order> pendingOrders = orderService.getPendingOrders();
        assertTrue(pendingOrders.contains(orderService.getOrderById(orderId)));
    }
    @Test
    public void canGetOrdersInProcessing() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        List<Order> processingOrders = orderService.getProcessingOrders();
        assertTrue(processingOrders.contains(orderService.getOrderById(orderId)));
    }

    @Test
    public void canRejectOrder() {
        int orderId = TestUtils.submitOrder();
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.PENDING);
        orderService.rejectOrder(orderId);
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantRejectProcessingOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.rejectOrder(orderId);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantRejectCompletedOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        orderService.completeOrder(orderId);
        orderService.rejectOrder(orderId);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantRejectRejectedOrder() {
        int orderId = TestUtils.submitOrder();
        orderService.rejectOrder(orderId);
        orderService.rejectOrder(orderId);
    }

    private Map<Integer,Integer> incrementItemQuantities(Map<Integer, Integer> originalItems) {
        Map<Integer, Integer> newItems = new TreeMap<>();
        for (Map.Entry<Integer, Integer> entry : originalItems.entrySet()) {
            newItems.put(entry.getKey(), entry.getValue() + 1);
        }
        return newItems;
    }

    private void assertDateLessThan5SecondsOld(LocalDateTime orderDateTime) {
        assertTrue(orderDateTime.toInstant(ZoneOffset.UTC).isAfter(LocalDateTime.now().minusSeconds(5).toInstant(ZoneOffset.UTC)));
    }
}
