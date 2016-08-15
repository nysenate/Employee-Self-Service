package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.Requisition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Returns information on the number of items in the given requisitions.
 */
public class ItemStatistic extends SupplyStatistic {

    public ItemStatistic(List<Requisition> requisitions) {
        super(requisitions);
    }

    /**
     * Returns a Map of item commodity codes to total quantity.
     * Only items in {@link #requisitions} are included.
     * Requisition LineItems with a quantity of 0 are ignored.
     */
    @Override
    public Map<String, Integer> calculate() {
        Map<String, Integer> totalQuantities = new HashMap<>();
        for (Requisition req : this.requisitions) {
            addRequisitionQuantitiesToTotalQuantities(requisitionQuantities(req), totalQuantities);
        }
        return totalQuantities;
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
