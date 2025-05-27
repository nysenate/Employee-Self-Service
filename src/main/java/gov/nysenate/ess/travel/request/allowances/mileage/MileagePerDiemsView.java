package gov.nysenate.ess.travel.request.allowances.mileage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Collectors;

public class MileagePerDiemsView implements ViewObject {

    private String totalPerDiem;
    private List<MileagePerDiemView> allPerDiems;
    private List<MileagePerDiemView> qualifyingPerDiems;
    private List<MileagePerDiemView> requestedPerDiems;
    @JsonProperty("doesTripQualifyForReimbursement")
    private boolean doesTripQualifyForReimbursement;
    private String totalMileage;
    @JsonProperty("isOverridden")
    private boolean isOverridden;
    private String overrideRate;

    public MileagePerDiemsView() {
    }

    public MileagePerDiemsView(MileagePerDiems mpd) {
        this.totalPerDiem = mpd.totalPerDiemValue().toString();
        this.allPerDiems = mpd.allPerDiems().stream()
                .map(MileagePerDiemView::new)
                .collect(Collectors.toList());
        this.qualifyingPerDiems = mpd.qualifyingPerDiems().stream()
                .map(MileagePerDiemView::new)
                .collect(Collectors.toList());
        this.requestedPerDiems = mpd.requestedPerDiems().stream()
                .map(MileagePerDiemView::new)
                .collect(Collectors.toList());
        this.doesTripQualifyForReimbursement = mpd.tripQualifiesForReimbursement();
        this.totalMileage = String.valueOf(mpd.totalMileage());
        this.isOverridden = mpd.isOverridden();
        this.overrideRate = mpd.getOverrideRate().toString();
    }

    public MileagePerDiems toMileagePerDiems() {
        return new MileagePerDiems(allPerDiems.stream()
                .map(MileagePerDiemView::toMileagePerDiem)
                .collect(Collectors.toList()),
                new Dollars(overrideRate)
        );
    }

    public String getTotalPerDiem() {
        return totalPerDiem;
    }

    public List<MileagePerDiemView> getAllPerDiems() {
        return allPerDiems;
    }

    public List<MileagePerDiemView> getQualifyingPerDiems() {
        return qualifyingPerDiems;
    }

    public List<MileagePerDiemView> getRequestedPerDiems() {
        return requestedPerDiems;
    }

    public boolean isDoesTripQualifyForReimbursement() {
        return doesTripQualifyForReimbursement;
    }

    public String getTotalMileage() {
        return totalMileage;
    }

    @JsonIgnore
    public boolean isOverridden() {
        return isOverridden;
    }

    public String getOverrideRate() {
        return overrideRate;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
