package gov.nysenate.ess.supply.statistics.location;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.statistics.SupplyStatistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Calculates the quantity of each item ordered at this location from the supplied {@link #requisitions}.
 */
public class LocationStatistic extends SupplyStatistic {

    private Location location;

    public LocationStatistic(Location location, List<Requisition> requisitions) {
        super(requisitions);
        this.location = checkNotNull(location);
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public Map<String, Integer> calculate() {
        Map<String, Integer> totalQuantities = new HashMap<>();
        for (Requisition req : requisitionsWithDestination(location)) {
            addRequisitionQuantitiesToTotalQuantities(requisitionQuantities(req), totalQuantities);
        }
        return totalQuantities;
    }

    private List<Requisition> requisitionsWithDestination(Location location) {
        return this.requisitions.stream()
                                .filter(r -> r.getDestination().getLocId() == location.getLocId())
                                .collect(Collectors.toList());
    }

    private void addRequisitionQuantitiesToTotalQuantities(Map<String, Integer> requisitionQuantities, Map<String, Integer> totalQuantities) {
        for (Map.Entry<String, Integer> entry : requisitionQuantities.entrySet()) {
            if (totalQuantities.containsKey(entry.getKey())) {
                int currentQuantity = totalQuantities.get(entry.getKey());
                totalQuantities.put(entry.getKey(), currentQuantity + requisitionQuantities.get(entry.getKey()));
            } else {
                totalQuantities.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private Map<String, Integer> requisitionQuantities(Requisition requisition) {
        Map<String, Integer> requisitionQuantities = new HashMap<>();
        Set<LineItem> positiveLineItems = lineItemsWithPositiveQuantities(requisition);
        for (LineItem lineItem : positiveLineItems) {
            requisitionQuantities.put(lineItem.getItem().getCommodityCode(), lineItem.getQuantity());
        }
        return requisitionQuantities;
    }

    private Set<LineItem> lineItemsWithPositiveQuantities(Requisition req) {
        return req.getLineItems().stream().filter(li -> li.getQuantity() > 0).collect(Collectors.toSet());
    }
}
