package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.service.OrderService;
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
    @Autowired
    private TestUtils testUtils;

    @Test
    public void newOrderShouldBeGivenId() {
        int id = testUtils.submitOrder();
        assertTrue(id > 0);
    }

    @Test
    public void newOrderDateTimeShouldBeInitialized() {
        int id = testUtils.submitOrder();
        Order order = orderService.getOrderById(id);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
    }

    @Test
    public void newOrderShouldHavePendingStatus() {
        int id = testUtils.submitOrder();
        Order order = orderService.getOrderById(id);
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canGetOrderById() {
        int id = testUtils.submitOrder();
        Order order = orderService.getOrderById(id);
        assertNotNull(order);
    }

    @Test
    public void canGetAllOrders() {
        int id = testUtils.submitOrder();
        assertTrue(orderService.getOrders().contains(orderService.getOrderById(id)));
    }

    @Test
    public void canGetOrdersInProcessing() {
        int id = testUtils.submitOrder();
        orderService.processOrder(id, new Employee());
        List<Order> processingOrders = orderService.getProcessingOrders();
        assertTrue(processingOrders.contains(orderService.getOrderById(id)));
    }

    @Test
    public void canEditOrderItems() {
        int id = testUtils.submitOrder();
        Map<Integer, Integer> originalItems = orderService.getOrderById(id).getItems();
        Map<Integer, Integer> newItems = incrementItemQuantities(originalItems);
        orderService.updateOrderItems(id, newItems);
        assertEquals(orderService.getOrderById(id).getItems(), newItems);
    }

    @Test
    public void canRejectOrder() {
        int id = testUtils.submitOrder();
        assertEquals(orderService.getOrderById(id).getStatus(), OrderStatus.PENDING);
        orderService.rejectOrder(id);
        assertEquals(orderService.getOrderById(id).getStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void processingOrderShouldUpdateStatus() {
        int id = testUtils.submitOrder();
        assertEquals(orderService.getOrderById(id).getStatus(), OrderStatus.PENDING);
        orderService.processOrder(id, new Employee());
        assertEquals(orderService.getOrderById(id).getStatus(), OrderStatus.PROCESSING);
    }

    @Test
    public void processingOrderSetsIssuingEmployee() {
        int id = testUtils.submitOrder();
        assertNull(orderService.getOrderById(id).getIssuingEmployee());
        Employee issuingEmployee = testUtils.createEmployee();
        orderService.processOrder(id, issuingEmployee);
        assertEquals(orderService.getOrderById(id).getIssuingEmployee(), issuingEmployee);
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
