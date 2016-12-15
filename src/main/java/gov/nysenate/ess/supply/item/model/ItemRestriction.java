package gov.nysenate.ess.supply.item.model;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.LocationId;

import java.util.Set;

public class ItemRestriction {

    private final ImmutableSet<LocationId> allowedLocations;

    public ItemRestriction(Set<LocationId> allowedLocations) {
        this.allowedLocations = allowedLocations == null
                ? ImmutableSet.of()
                : ImmutableSet.copyOf(allowedLocations);
    }

    boolean isRestricted() {
        return !allowedLocations.isEmpty();
    }

    boolean isAllowed(LocationId locId) {
        return allowedLocations.contains(locId);
    }

    @Override
    public String toString() {
        return "ItemRestriction{" +
                "allowedLocations=" + allowedLocations +
                '}';
    }
}
