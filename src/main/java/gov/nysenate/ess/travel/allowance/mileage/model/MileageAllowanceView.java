package gov.nysenate.ess.travel.allowance.mileage.model;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MileageAllowanceView implements ViewObject {

    private String rate;
    private String total;
    private ListView<ReimbursableLegView> outboundLegs;
    private ListView<ReimbursableLegView> returnLegs;

    public MileageAllowanceView(MileageAllowance allowance) {
        this.rate = allowance.getRate().toString();
        this.total = allowance.getAllowance().toString();
        this.outboundLegs = ListView.of(allowance.getOutboundLegs().stream()
                .map(ReimbursableLegView::new)
                .collect(Collectors.toList()));
        this.returnLegs = ListView.of(allowance.getReturnLegs().stream()
                .map(ReimbursableLegView::new)
                .collect(Collectors.toList()));
    }

    public MileageAllowance toMileageAllowance() {
        return new MileageAllowance(new BigDecimal(this.rate),
                ImmutableSet.copyOf(outboundLegs.items.stream().map(ReimbursableLegView::toReimbursableLeg).collect(Collectors.toList())),
                ImmutableSet.copyOf(returnLegs.items.stream().map(ReimbursableLegView::toReimbursableLeg).collect(Collectors.toList())));
    }

    public String getRate() {
        return rate;
    }

    public String getTotal() {
        return total;
    }

    public ListView<ReimbursableLegView> getOutboundLegs() {
        return outboundLegs;
    }

    public ListView<ReimbursableLegView> getReturnLegs() {
        return returnLegs;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
