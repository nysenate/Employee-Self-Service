package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class RouteView implements ViewObject{

    private final String mileageRate;
    private final boolean isMileageRequested;
    private final List<LegView> legs;

    public RouteView(Route route) {
        mileageRate = route.getMileageRate().toString();
        isMileageRequested = route.isMileageRequested();
        legs = route.getOutgoingLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
    }

    public String getMileageRate() {
        return mileageRate;
    }

    public boolean isMileageRequested() {
        return isMileageRequested;
    }

    public List<LegView> getLegs() {
        return legs;
    }

    @Override
    public String getViewType() {
        return "route";
    }
}
