package gov.nysenate.ess.supply.reconcilation.service;

import gov.nysenate.ess.supply.reconcilation.ReconciliationException;
import gov.nysenate.ess.supply.reconcilation.model.Inventory;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationError;
import gov.nysenate.ess.supply.reconcilation.model.ReconciliationResults;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class Reconciler {

    /**
     * Performs a reconciliation of two inventories. Verifies that the quantities for each item in the
     * {@code expected} inventory are the same as in the quantities in the {@code actual} inventory.
     *
     * This does NOT verify that both inventories are exactly equal, only that the quantities in
     * {@code expected} are in {@code actual}. Actual may have additional items which are not in {@code expected}.
     * @param expected An Inventory with item quantities entered by the user.
     * @param actual An Inventory with item quantities retrieved from sfms.
     * @return {@link ReconciliationResults}
     * @throws ReconciliationException if the inventory locations differ or if an item in {@code expected} does not exist in {@code actual}.
     */
    public ReconciliationResults reconcile(Inventory expected, Inventory actual) {
        if (!expected.getLocationId().equals(actual.getLocationId())) {
            throw new ReconciliationException("Inventory locations do not match, cannot perform reconciliation. \n" +
                    "Expected location " + expected.getLocationId().toString() + ". Actual location " + actual.getLocationId().toString());
        }

        Set<ReconciliationError> errors = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : expected.getItemQuantities().entrySet()) {
            int expectedQuantity = entry.getValue();
            int actualQuantity = getActualQuantity(entry.getKey(), actual);
            if (expectedQuantity != actualQuantity) {
                errors.add(new ReconciliationError(entry.getKey(), expectedQuantity, actualQuantity));
            }
        }

        return new ReconciliationResults(expected.getLocationId(), errors);
    }

    private int getActualQuantity(int itemId, Inventory actualInventory) {
        Integer actualQuantity = actualInventory.getItemQuantities().get(itemId);
        if (actualQuantity == null) {
            throw new ReconciliationException("Actual inventory missing expected item with id: " + itemId);
        }
        return actualQuantity;
    }
}
