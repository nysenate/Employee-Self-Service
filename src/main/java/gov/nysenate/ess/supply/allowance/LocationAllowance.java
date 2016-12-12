package gov.nysenate.ess.supply.allowance;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.Set;

/**
 * This class summarizes {@link SupplyItem} information that is specific to a {@link Location}.
 * It contains info on which supply items should be visible or marked special when ordering from this location.
 * It contains the allowable per order and per month order quantities for each item.
 * It also contains the quantity of an item ordered month to date for the location.
 */
public class LocationAllowance {

    private Location location;
    private Set<ItemAllowance> itemAllowances;

    public LocationAllowance(Location location, Set<ItemAllowance> itemAllowances) {
        this.location = location;
        this.itemAllowances = itemAllowances;
    }

    public Location getLocation() {
        return location;
    }

    public Set<ItemAllowance> getItemAllowances() {
        return itemAllowances;
    }
}
