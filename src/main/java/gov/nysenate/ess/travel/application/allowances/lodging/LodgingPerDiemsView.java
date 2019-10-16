package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class LodgingPerDiemsView implements ViewObject {

    private List<LodgingPerDiemView> allLodgingPerDiems;
    private List<LodgingPerDiemView> requestedLodgingPerDiems;
    private String requestedPerDiem;
    private String maximumPerDiem;

    public LodgingPerDiemsView() {
    }

    public LodgingPerDiemsView(LodgingPerDiems la) {
        this.allLodgingPerDiems = la.allLodgingPerDiems().stream()
                .map(LodgingPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedLodgingPerDiems = la.requestedLodgingPerDiems().stream()
                .map(LodgingPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedPerDiem = la.requestedPerDiem().toString();
        this.maximumPerDiem = la.maximumPerDiem().toString();
    }

    public LodgingPerDiems toLodgingPerDiems() {
        return new LodgingPerDiems(allLodgingPerDiems.stream()
                .map(LodgingPerDiemView::toLodgingPerDiem)
                .collect(Collectors.toList()));
    }

    public List<LodgingPerDiemView> getAllLodgingPerDiems() {
        return allLodgingPerDiems;
    }

    public List<LodgingPerDiemView> getRequestedLodgingPerDiems() {
        return requestedLodgingPerDiems;
    }

    public String getRequestedPerDiem() {
        return requestedPerDiem;
    }

    public String getMaximumPerDiem() {
        return maximumPerDiem;
    }

    @Override
    public String getViewType() {
        return "lodging-allowances";
    }
}
