package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.SupplyTests;
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

    @Test
    public void newOrderShouldBeGivenId() {
        int id = submitNewOrder();
        assertTrue(id > 0);
    }

    @Test
    public void newOrderDateTimeShouldBeInitialized() {
        int id = submitNewOrder();
        Order order = orderService.getOrderById(id);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
    }

    @Test
    public void newOrderShouldHavePendingStatus() {
        int id = submitNewOrder();
        Order order = orderService.getOrderById(id);
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canGetOrderById() {
        int id = submitNewOrder();
        Order order = orderService.getOrderById(id);
        assertNotNull(order);
    }

    private int submitNewOrder() {
        return orderService.submitOrder(createEmployee(), createLocation(), orderedItemsToQuantitiesMap());
    }

    private Employee createEmployee() {
        Employee emp = new Employee();
        emp.setUid("JOHNSON");
        return emp;
    }

    private Location createLocation() {
        Location location = new Location();
        location.setCode("AF2FB");
        location.setType(LocationType.WORK);
        return location;
    }

    private Map<String, Integer> orderedItemsToQuantitiesMap() {
        TreeMap<String, Integer> orderedItemsToQuantities = new TreeMap<>();
        for (SupplyItem item : itemService.getSupplyItems()) {
            orderedItemsToQuantities.put(item.getCommodityCode(), 1);
        }
        return orderedItemsToQuantities;
    }

    private void assertDateLessThan5SecondsOld(LocalDateTime orderDateTime) {
        assertTrue(orderDateTime.toInstant(ZoneOffset.UTC).isAfter(LocalDateTime.now().minusSeconds(5).toInstant(ZoneOffset.UTC)));
    }
}
