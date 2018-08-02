package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class LodgingAllowancesView implements ViewObject {

    List<LodgingAllowanceView> lodgingAllowances;
    String totalLodgingAllowance;

    public LodgingAllowancesView() {
    }

    public LodgingAllowancesView(LodgingAllowances lodgingAllowances) {
        this.lodgingAllowances = lodgingAllowances.getLodgingAllowances().stream()
                .map(LodgingAllowanceView::new)
                .collect(Collectors.toList());
        this.totalLodgingAllowance = lodgingAllowances.totalAllowance().toString();
    }

    public LodgingAllowances toLodgingAllowances() {
        return new LodgingAllowances(lodgingAllowances.stream()
                .map(LodgingAllowanceView::toLodgingAllowance)
                .collect(Collectors.toList()));
    }

    public List<LodgingAllowanceView> getLodgingAllowances() {
        return lodgingAllowances;
    }

    public String getTotalLodgingAllowance() {
        return totalLodgingAllowance;
    }

    @Override
    public String getViewType() {
        return "lodging-allowances";
    }
}
