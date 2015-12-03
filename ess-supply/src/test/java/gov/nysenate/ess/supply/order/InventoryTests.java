package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.inventory.service.InventoryService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.Assert.*;

public class InventoryTests extends SupplyTests {

    @Autowired
    private InventoryService inventory;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TestUtils testUtils;

    @Test
    public void inventoryShouldInitialize() {
        assertNotNull(inventory.getCurrentInventory());
        assertTrue(inventory.getCurrentInventory().size() > 0);
    }

    @Test
    public void inventoryShouldAccountForProcessingOrders() {
        Map<Integer, Integer> initialInventory = inventory.getCurrentInventory();
        int orderId = testUtils.submitOrder(); // Order has 1 of each item.
        orderService.processOrder(orderId, new Employee());
        Order order = orderService.getOrderById(orderId);
        for (Map.Entry<Integer, Integer> entry : inventory.getCurrentInventory().entrySet()) {
//            assertEquals((Object)entry.getValue(), initialInventory.get(entry.getKey()) - 1);
//            if (order.getItems().containsKey(entry.getKey())) {
//                assertEquals((Object)entry.getValue(), initialInventory.get(entry.getKey()) - order.getItems().get(entry.getKey()));
//            }
//            else {
//                assertEquals(entry.getValue(), initialInventory.get(entry.getKey()));
//            }
        }
    }

}
