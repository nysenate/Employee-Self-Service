package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class OrderTests extends SupplyTests {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplyItemService itemService;

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
    public void canGetOrdersInProcessing() {
        int orderId = TestUtils.submitOrder();
        orderService.processOrder(orderId, new Employee());
        List<Order> processingOrders = orderService.getProcessingOrders();
        assertTrue(processingOrders.contains(orderService.getOrderById(orderId)));
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
    public void canRejectOrder() {
        int orderId = TestUtils.submitOrder();
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.PENDING);
        orderService.rejectOrder(orderId);
        assertEquals(orderService.getOrderById(orderId).getStatus(), OrderStatus.REJECTED);
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
