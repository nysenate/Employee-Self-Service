package gov.nysenate.ess.supply;

import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.InMemorySupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.dao.InMemoryOrder;
import gov.nysenate.ess.supply.order.dao.SfmsInMemoryOrder;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TestUtils {

    private static SupplyItemService itemService;
    private static InMemoryOrder orderDao;
    private static SfmsInMemoryOrder sfmsDao;
    private static InMemorySupplyItem supplyItemDao;
    private static OrderService orderService;

    @Autowired
    public TestUtils(SupplyItemService itemService, InMemoryOrder orderDao,
                     SfmsInMemoryOrder sfmsDao, InMemorySupplyItem supplyItemDao, OrderService orderService) {
        this.itemService = itemService;
        this.orderDao = orderDao;
        this.sfmsDao = sfmsDao;
        this.supplyItemDao = supplyItemDao;
        this.orderService = orderService;
    }

    public static void resetInMemoryDaos() {
        orderDao.reset();
        sfmsDao.reset();
        supplyItemDao.reset();
    }

    public static Set<LineItem> orderedItemsToQuantitiesMap() {
        Set<LineItem> orderedItemsToQuantities = new HashSet<>();
        for (SupplyItem item : itemService.getSupplyItems()) {
            orderedItemsToQuantities.add(new LineItem(item.getId(), 1));
        }
        return orderedItemsToQuantities;
    }

    public static Order submitOrder() {
        return orderService.submitOrder(1, TestUtils.orderedItemsToQuantitiesMap());
    }

    public static Order submitAndProcessOrder() {
        Order order = submitOrder();
        return orderService.processOrder(order.getId(), 2);
    }

    public static Order submitProcessAndCompleteOrder() {
        Order order = submitAndProcessOrder();
        return orderService.completeOrder(order.getId());
    }
}
