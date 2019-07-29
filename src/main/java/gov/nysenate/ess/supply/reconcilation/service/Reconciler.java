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
     * Performs a reconciliation of two inventories. Verifies that the quantities for each item in
     * {@code inventory} are the same as in the quantities in the {@code expected}.
     *
     * This does NOT verify that both inventories are exactly equal, only that the quantities in
     * {@code inventory} are in {@code expected}. Expected may have additional items which are not in {@code inventory}.
     *
     * @param inventory An Inventory with item quantities entered by the user.
     * @param expected An Inventory with item quantities retrieved from sfms.
     * @return {@link ReconciliationResults}
     */
    public ReconciliationResults reconcile(Inventory inventory, Inventory expected) {
        Set<ReconciliationError> errors = new HashSet<>();
        for (Map.Entry<Integer, Integer> entry : inventory.getItemQuantities().entrySet()) {
            int quantity = entry.getValue();
            int expectedQuantity = getExpectedQuantity(entry.getKey(), expected);
            if (quantity != expectedQuantity) {
                errors.add(new ReconciliationError(entry.getKey(), quantity, expectedQuantity));
            }
        }
        return new ReconciliationResults(errors);
    }

    private int getExpectedQuantity(int itemId, Inventory expectedInventory) {
        Integer expectedQuantity = expectedInventory.getItemQuantities().get(itemId);
        if (expectedQuantity == null) {
            throw new ReconciliationException("Expected(SFMS) inventory missing item with id: " + itemId);
        }
        return expectedQuantity;
    }
}
