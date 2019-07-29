package gov.nysenate.ess.supply.reconcilation.model;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.Map;

public class InventoryView implements ViewObject {

    protected Map<Integer, Integer> itemQuantities;

    public InventoryView() {
    }

    public InventoryView(Inventory inventory) {
        this.itemQuantities = inventory.getItemQuantities();
    }

    public Inventory toInventory() {
        return new Inventory(itemQuantities);
    }

    public Map<Integer, Integer> getItemQuantities() {
        return itemQuantities;
    }

    @Override
    public String getViewType() {
        return "inventory-view";
    }
}
