package gov.nysenate.ess.travel.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class RouteView implements ViewObject{

    private String mileageRate;
    private List<LegView> outboundLegs;
    private List<LegView> returnLegs;
    private String mileageAllowance;
    private AddressView origin;

    public RouteView() {
    }

    public RouteView(Route route) {
        mileageRate = route.getMileageRate().toString();
        outboundLegs = route.getOutgoingLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        returnLegs = route.getReturnLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        mileageAllowance = route.mileageAllowance().toString();
        origin = new AddressView(route.origin());
    }

    public Route toRoute() {
        return new Route(outboundLegs.stream().map(LegView::toLeg).collect(ImmutableList.toImmutableList()),
                returnLegs.stream().map(LegView::toLeg).collect(ImmutableList.toImmutableList()),
                new BigDecimal(mileageRate));
    }

    public String getMileageRate() {
        return mileageRate;
    }

    public List<LegView> getOutboundLegs() {
        return outboundLegs;
    }

    public List<LegView> getReturnLegs() {
        return returnLegs;
    }

    public String getMileageAllowance() {
        return mileageAllowance;
    }

    public AddressView getOrigin() {
        return origin;
    }

    @Override
    public String getViewType() {
        return "route";
    }
}
