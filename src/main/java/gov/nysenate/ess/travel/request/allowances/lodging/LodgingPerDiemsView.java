package gov.nysenate.ess.travel.request.allowances.lodging;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Collectors;

public class LodgingPerDiemsView implements ViewObject {

    private List<LodgingPerDiemView> allLodgingPerDiems;
    private List<LodgingPerDiemView> requestedLodgingPerDiems;
    private String totalPerDiem;
    private boolean isOverridden;
    private double overrideRate;

    public LodgingPerDiemsView() {
    }

    public LodgingPerDiemsView(LodgingPerDiems la) {
        this.allLodgingPerDiems = la.allLodgingPerDiems().stream()
                .map(LodgingPerDiemView::new)
                .collect(Collectors.toList());
        this.requestedLodgingPerDiems = la.requestedLodgingPerDiems().stream()
                .map(LodgingPerDiemView::new)
                .collect(Collectors.toList());
        this.totalPerDiem = la.totalPerDiem().toString();
        this.isOverridden = la.isOverridden();
        this.overrideRate = Double.parseDouble(la.overrideRate().toString());
    }

    public LodgingPerDiems toLodgingPerDiems() {
        return new LodgingPerDiems(allLodgingPerDiems.stream()
                .map(LodgingPerDiemView::toLodgingPerDiem)
                .collect(Collectors.toList()), new Dollars(overrideRate));
    }

    public List<LodgingPerDiemView> getAllLodgingPerDiems() {
        return allLodgingPerDiems;
    }

    public List<LodgingPerDiemView> getRequestedLodgingPerDiems() {
        return requestedLodgingPerDiems;
    }

    public String getTotalPerDiem() {
        return totalPerDiem;
    }

    @JsonProperty("isOverridden")
    public boolean isOverridden() {
        return isOverridden;
    }

    public double getOverrideRate() {
        return overrideRate;
    }

    @Override
    public String getViewType() {
        return "lodging-allowances";
    }
}
