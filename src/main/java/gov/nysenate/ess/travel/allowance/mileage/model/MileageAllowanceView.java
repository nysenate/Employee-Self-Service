package gov.nysenate.ess.travel.allowance.mileage.model;

import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MileageAllowanceView implements ViewObject {

    private String rate;
    private String total;
    private ListView<ReimbursableLegView> legs;

    public MileageAllowanceView(MileageAllowance total) {
        this.rate = total.getRate().toString();
        this.total = total.getAllowance().toString();
        this.legs = ListView.of(
                Stream.concat(total.getOutboundLegs().stream(), total.getReturnLegs().stream())
                .map(ReimbursableLegView::new)
                .collect(Collectors.toList()));
    }

    public String getRate() {
        return rate;
    }

    public String getTotal() {
        return total;
    }

    public ListView<ReimbursableLegView> getLegs() {
        return legs;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
