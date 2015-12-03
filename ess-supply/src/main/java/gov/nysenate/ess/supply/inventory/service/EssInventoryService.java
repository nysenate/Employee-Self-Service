package gov.nysenate.ess.supply.inventory.service;

import gov.nysenate.ess.supply.inventory.dao.InventoryDao;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EssInventoryService implements InventoryService {

    @Autowired private InventoryDao inventoryDao;
    @Autowired private OrderService orderService;

    @Override
    public Map<Integer, Integer> getCurrentInventory() {
        Map<Integer, Integer> inventory = inventoryDao.getCurrentInventory();
        List<Order> processingOrders = orderService.getProcessingOrders();
        for (Order order : processingOrders) {
            for (int itemId : order.getItems().keySet()) {
                inventory.put(itemId, inventory.get(itemId) - order.getItems().get(itemId));
            }
        }
        return inventory;
    }
}
