package gov.nysenate.ess.supply.reconcilation.model;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.LocationId;

import java.util.Map;

public class InventoryView implements ViewObject {

    protected String locationCode;
    protected String locationType;
    protected Map<Integer, Integer> itemQuantities;

    public InventoryView() {
    }

    public InventoryView(Inventory inventory) {
        this.locationCode = inventory.getLocationId().getCode();
        this.locationType = inventory.getLocationId().getType().getCode() + "";
        this.itemQuantities = inventory.getItemQuantities();
    }

    public Inventory toInventory() {
        return new Inventory(new LocationId(locationCode, locationType.charAt(0)), itemQuantities);
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getLocationType() {
        return locationType;
    }

    public Map<Integer, Integer> getItemQuantities() {
        return itemQuantities;
    }

    @Override
    public String getViewType() {
        return "inventory-view";
    }
}
