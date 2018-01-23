package gov.nysenate.ess.travel.allowance.mileage.model;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MileageAllowanceView implements ViewObject {

    private String rate;
    private String total;
    private Set<ReimbursableLegView> outboundLegs;
    private Set<ReimbursableLegView> returnLegs;

    public MileageAllowanceView() {
    }

    public MileageAllowanceView(MileageAllowance allowance) {
        this.rate = allowance.getRate().toString();
        this.total = allowance.getAllowance().toString();
        this.outboundLegs = allowance.getOutboundLegs().stream()
                .map(ReimbursableLegView::new)
                .collect(Collectors.toSet());
        this.returnLegs = allowance.getReturnLegs().stream()
                .map(ReimbursableLegView::new)
                .collect(Collectors.toSet());
    }

    public MileageAllowance toMileageAllowance() {
        return new MileageAllowance(new BigDecimal(this.rate),
                ImmutableSet.copyOf(outboundLegs.stream().map(ReimbursableLegView::toReimbursableLeg).collect(Collectors.toList())),
                ImmutableSet.copyOf(returnLegs.stream().map(ReimbursableLegView::toReimbursableLeg).collect(Collectors.toList())));
    }

    public String getRate() {
        return rate;
    }

    public String getTotal() {
        return total;
    }

    public Set<ReimbursableLegView> getOutboundLegs() {
        return outboundLegs;
    }

    public Set<ReimbursableLegView> getReturnLegs() {
        return returnLegs;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
