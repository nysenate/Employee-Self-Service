package gov.nysenate.ess.supply;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.dao.InMemorySupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.dao.InMemoryOrder;
import gov.nysenate.ess.supply.order.dao.SfmsInMemoryOrder;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
public class TestUtils {

    private static OrderService orderService;
    private static SupplyItemService itemService;
    private static InMemoryOrder orderDao;
    private static SfmsInMemoryOrder sfmsDao;
    private static InMemorySupplyItem supplyItemDao;

    @Autowired
    public TestUtils(OrderService orderService, SupplyItemService itemService, InMemoryOrder orderDao,
                     SfmsInMemoryOrder sfmsDao, InMemorySupplyItem supplyItemDao) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.orderDao = orderDao;
        this.sfmsDao = sfmsDao;
        this.supplyItemDao = supplyItemDao;
    }

    public static void resetInMemoryDaos() {
        orderDao.reset();
        sfmsDao.reset();
        supplyItemDao.reset();
    }

    public static int submitOrder() {
        return orderService.submitOrder(createEmployee(), createLocation(), orderedItemsToQuantitiesMap());
    }

    public static Employee createEmployee() {
        Employee emp = new Employee();
        emp.setUid("JOHNSON");
        return emp;
    }

    private static Location createLocation() {
        Location location = new Location();
        location.setCode("AF2FB");
        location.setType(LocationType.WORK);
        return location;
    }

    private static Map<Integer, Integer> orderedItemsToQuantitiesMap() {
        TreeMap<Integer, Integer> orderedItemsToQuantities = new TreeMap<>();
        for (SupplyItem item : itemService.getSupplyItems()) {
            orderedItemsToQuantities.put(item.getId(), 1);
        }
        return orderedItemsToQuantities;
    }
}
