package gov.nysenate.ess.supply;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

@Service
public class TestUtils {

    @Autowired OrderService orderService;
    @Autowired SupplyItemService itemService;

    public int submitOrder() {
        return orderService.submitOrder(createEmployee(), createLocation(), orderedItemsToQuantitiesMap());
    }

    public Employee createEmployee() {
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

    private Map<Integer, Integer> orderedItemsToQuantitiesMap() {
        TreeMap<Integer, Integer> orderedItemsToQuantities = new TreeMap<>();
        for (SupplyItem item : itemService.getSupplyItems()) {
            orderedItemsToQuantities.put(item.getId(), 1);
        }
        return orderedItemsToQuantities;
    }
}
