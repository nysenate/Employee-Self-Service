package gov.nysenate.ess.travel.application.route;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Streams;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleRouteView implements ViewObject {

    private List<SimpleLegView> outboundLegs;
    private List<SimpleLegView> returnLegs;
    private List<AddressView> destinations;
    private AddressView origin;

//    private String totalMiles;
//    private String mileageExpense;

//    private MealAllowancesView mealAllowances;
//    private LodgingAllowancesView lodgingAllowances;

    public SimpleRouteView() {
    }

    public SimpleRouteView(Route route) {
        outboundLegs = route.getOutboundLegs().stream()
                .map(SimpleLegView::new)
                .collect(Collectors.toList());
        returnLegs = route.getReturnLegs().stream()
                .map(SimpleLegView::new)
                .collect(Collectors.toList());
        origin = route.origin() == null ? null : new AddressView(route.origin().getAddress());
//        totalMiles = String.valueOf(route.totalMiles());
//        mileageExpense = route.mileageExpense().toString();
        destinations = route.destinations().stream()
                .map(d -> new AddressView(d.getAddress()))
                .collect(Collectors.toList());

//        this.mealAllowances = new MealAllowancesView(route.mealAllowances());
//        this.lodgingAllowances = new LodgingAllowancesView(route.lodgingAllowances());
    }

    @JsonIgnore
    public List<SimpleLegView> getAllLegs() {
        return Streams.concat(getOutboundLegs().stream(), getReturnLegs().stream())
                .collect(Collectors.toList());
    }

    public List<SimpleLegView> getOutboundLegs() {
        return outboundLegs;
    }

    public List<SimpleLegView> getReturnLegs() {
        return returnLegs;
    }

    public List<AddressView> getDestinations() {
        return destinations;
    }

    public AddressView getOrigin() {
        return origin;
    }

    @Override
    public String getViewType() {
        return "simple-route";
    }
}
