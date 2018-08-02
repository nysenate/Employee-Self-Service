package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class MileageAllowancesView implements ViewObject {

    List<MileageAllowanceView> outboundAllowances;
    List<MileageAllowanceView> returnAllowances;
    String totalMileageAllowance;

    public MileageAllowancesView() {
    }

    public MileageAllowancesView(MileageAllowances mileageAllowances) {
        this.outboundAllowances = mileageAllowances.getOutboundAllowances().stream()
                .map(MileageAllowanceView::new)
                .collect(Collectors.toList());
        this.returnAllowances = mileageAllowances.getReturnAllowances().stream()
                .map(MileageAllowanceView::new)
                .collect(Collectors.toList());
        this.totalMileageAllowance = mileageAllowances.totalAllowance().toString();
    }

    public MileageAllowances toMileageAllowanceView() {
        return new MileageAllowances(getOutboundAllowances().stream()
                .map(MileageAllowanceView::toLegMileageAllowance)
                .collect(Collectors.toList()),
                returnAllowances.stream()
                        .map(MileageAllowanceView::toLegMileageAllowance)
                        .collect(Collectors.toList()));
    }

    public List<MileageAllowanceView> getOutboundAllowances() {
        return outboundAllowances;
    }

    public List<MileageAllowanceView> getReturnAllowances() {
        return returnAllowances;
    }

    public String getTotalMileageAllowance() {
        return totalMileageAllowance;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
