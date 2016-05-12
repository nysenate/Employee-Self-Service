package gov.nysenate.ess.supply.allowance.view;

import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.allowance.LocationAllowance;

import java.util.stream.Collectors;

public class LocationAllowanceView implements ViewObject {

    protected LocationView location;
    protected ListView<ItemAllowanceView> itemAllowances;

    public LocationAllowanceView(LocationAllowance locationAllowance) {
        this.location = new LocationView(locationAllowance.getLocation());
        this.itemAllowances = ListView.of(locationAllowance.getItemAllowances()
                                                           .stream()
                                                           .map(ItemAllowanceView::new)
                                                           .collect(Collectors.toList()));
    }

    public LocationAllowance toLocationAllowance() {
        return new LocationAllowance(location.toLocation(), itemAllowances.items.stream()
                                                                                .map(ItemAllowanceView::toItemAllowance)
                                                                                .collect(Collectors.toSet()));
    }

    public LocationView getLocation() {
        return location;
    }

    public ListView<ItemAllowanceView> getItemAllowances() {
        return itemAllowances;
    }

    @Override
    public String getViewType() {
        return "location-profile";
    }
}
