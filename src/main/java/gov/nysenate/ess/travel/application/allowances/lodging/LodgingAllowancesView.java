package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class LodgingAllowancesView implements ViewObject {

    private List<LodgingPerDiemView> allLodgingPerDiems;
    private List<LodgingPerDiemView> requestedLodgingPerDiems;
    private String totalRequestedAllowance;

    public LodgingAllowancesView() {
    }

    public LodgingAllowancesView(LodgingAllowances la) {
        this.allLodgingPerDiems = la.allLodgingPerDiems().stream()
                .map(LodgingPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedLodgingPerDiems = la.requestedLodgingPerDiems().stream()
                .map(LodgingPerDiemView::new)
                .collect(Collectors.toList());
        this.totalRequestedAllowance = la.totalRequestedAllowance().toString();
    }

    public List<LodgingPerDiemView> getAllLodgingPerDiems() {
        return allLodgingPerDiems;
    }

    public List<LodgingPerDiemView> getRequestedLodgingPerDiems() {
        return requestedLodgingPerDiems;
    }

    public String getTotalRequestedAllowance() {
        return totalRequestedAllowance;
    }

    @Override
    public String getViewType() {
        return "lodging-allowances";
    }
}
