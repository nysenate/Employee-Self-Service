package gov.nysenate.ess.travel.allowance.gsa.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;

import java.util.Set;
import java.util.stream.Collectors;

public class LodgingAllowanceView implements ViewObject {

    private String total;
    private Set<LodgingNightView> lodgingNights;

    public LodgingAllowanceView() {
    }

    public LodgingAllowanceView(LodgingAllowance allowance) {
        this.total = allowance.getTotal().toString();
        lodgingNights = allowance.getNights().stream()
                .map(LodgingNightView::new)
                .collect(Collectors.toSet());
    }

    public LodgingAllowance toLodgingAllowance() {
        return new LodgingAllowance(lodgingNights.stream()
                .map(LodgingNightView::toLodgingNight)
                .collect(Collectors.toSet()));
    }

    public String getTotal() {
        return total;
    }

    public Set<LodgingNightView> getLodgingNights() {
        return lodgingNights;
    }

    @Override
    public String getViewType() {
        return "lodging-allowance";
    }
}
