package gov.nysenate.ess.travel.application.allowances.mileage;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.route.LegView;

import java.util.List;
import java.util.stream.Collectors;

public class MileageAllowancesView implements ViewObject {

    private String requestedAllowance;
    private String maximumAllowance;
    private List<LegView> legs;

    public MileageAllowancesView() {
    }

    public MileageAllowancesView(MileageAllowances ma) {
        this.maximumAllowance = ma.maximumAllowance().toString();
        this.requestedAllowance = ma.requestedAllowance().toString();
        this.legs = ma.allLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
    }

    public String getRequestedAllowance() {
        return requestedAllowance;
    }

    public String getMaximumAllowance() {
        return maximumAllowance;
    }

    public List<LegView> getLegs() {
        return legs;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
