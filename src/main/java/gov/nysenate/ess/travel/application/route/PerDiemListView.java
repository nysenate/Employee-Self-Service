package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class PerDiemListView implements ViewObject {

    List<PerDiemView> perDiems;
    String total;

    public PerDiemListView() {
    }

    public PerDiemListView(PerDiemList perDiemList) {
        this.total = perDiemList.total().toString();
        this.perDiems = perDiemList.getPerDiems().stream()
                .map(PerDiemView::new)
                .collect(Collectors.toList());
    }

    public List<PerDiemView> getPerDiems() {
        return perDiems;
    }

    public String getTotal() {
        return total;
    }

    @Override
    public String getViewType() {
        return "per-diem-list";
    }
}
