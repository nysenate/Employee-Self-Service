package gov.nysenate.ess.travel.application.allowances.mileage;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.route.LegView;

import java.util.List;
import java.util.stream.Collectors;

public class MileagePerDiemsView implements ViewObject {

    private String totalPerDiem;
    private List<LegView> allLegs;
    private List<LegView> qualifyingLegs;
    private List<LegView> requestedLegs;
    @JsonProperty("doesTripQualifyForReimbursement")
    private boolean doesTripQualifyForReimbursement;
    private String totalMileage;

    public MileagePerDiemsView() {
    }

    public MileagePerDiemsView(MileagePerDiems ma) {
        this.totalPerDiem = ma.totalPerDiem().toString();
        this.allLegs = ma.allLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        this.qualifyingLegs = ma.mileageReimbursableLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        this.requestedLegs = ma.requestedLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        this.doesTripQualifyForReimbursement = ma.tripQualifiesForReimbursement();
        this.totalMileage = String.valueOf(ma.totalMileage());
    }

    public MileagePerDiems toMileagePerDiems() {
        return new MileagePerDiems(allLegs.stream().map(LegView::toLeg).collect(Collectors.toList()));
    }

    public String getTotalPerDiem() {
        return totalPerDiem;
    }

    public List<LegView> getAllLegs() {
        return allLegs;
    }

    public List<LegView> getQualifyingLegs() {
        return qualifyingLegs;
    }

    public List<LegView> getRequestedLegs() {
        return requestedLegs;
    }

    public boolean isDoesTripQualifyForReimbursement() {
        return doesTripQualifyForReimbursement;
    }

    public String getTotalMileage() {
        return totalMileage;
    }

    @Override
    public String getViewType() {
        return "mileage-allowance";
    }
}
