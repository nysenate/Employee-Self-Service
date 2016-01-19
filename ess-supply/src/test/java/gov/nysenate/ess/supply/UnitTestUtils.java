package gov.nysenate.ess.supply;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.InMemorySupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.dao.InMemoryOrder;
import gov.nysenate.ess.supply.order.dao.sfms.SfmsInMemoryOrder;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Util methods for unit tests. Uses mock dao's.
 */
@Service
public class UnitTestUtils {

    private static SupplyItemService itemService;
    private static InMemoryOrder orderDao;
    private static SfmsInMemoryOrder sfmsDao;
    private static InMemorySupplyItem supplyItemDao;
    private static OrderService orderService;

    @Autowired
    public UnitTestUtils(SupplyItemService itemService, InMemoryOrder orderDao, SfmsInMemoryOrder sfmsDao,
                         InMemorySupplyItem supplyItemDao, OrderService orderService) {
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
        for (SupplyItem item : itemService.getSupplyItems(LimitOffset.TEN)) {
            orderedItemsToQuantities.add(new LineItem(item.getId(), 1));
        }
        return orderedItemsToQuantities;
    }

    public static Order submitOrder() {
        return orderService.submitOrder(1, UnitTestUtils.orderedItemsToQuantitiesMap());
    }

    public static Order submitAndProcessOrder() {
        Order order = submitOrder();
        return orderService.processOrder(order.getId(), 2);
    }

    public static Order submitProcessAndCompleteOrder() {
        Order order = submitAndProcessOrder();
        return orderService.completeOrder(order.getId());
    }

    public static Range<LocalDate> getDateRange() {
        return Range.closed(LocalDate.MIN, LocalDate.now());
    }
}
