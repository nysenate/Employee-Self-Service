package gov.nysenate.ess.supply.allowance.view;

import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.allowance.ItemAllowance;
import gov.nysenate.ess.supply.allowance.LocationProfile;

import java.util.stream.Collectors;

public class LocationProfileView implements ViewObject {

    protected LocationView location;
    protected ListView<ItemAllowanceView> itemAllowances;

    public LocationProfileView(LocationProfile locationProfile) {
        this.location = new LocationView(locationProfile.getLocation());
        this.itemAllowances = ListView.of(locationProfile.getItemAllowances()
                                                         .stream()
                                                         .map(ItemAllowanceView::new)
                                                         .collect(Collectors.toList()));
    }

    public LocationProfile toLocationProfile() {
        return new LocationProfile(location.toLocation(), itemAllowances.items.stream()
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
