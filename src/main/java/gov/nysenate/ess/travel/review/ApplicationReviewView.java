package gov.nysenate.ess.travel.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.TravelApplicationView;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationReviewView implements ViewObject {

    private String appReviewId;
    private TravelApplicationView travelApplication;
    private String nextReviewerRole;
    private List<ActionView> actions;
    private boolean isShared;

    public ApplicationReviewView() {
    }

    public ApplicationReviewView(ApplicationReview appReview) {
        appReviewId = String.valueOf(appReview.getAppReviewId());
        travelApplication = new TravelApplicationView(appReview.application());
        nextReviewerRole = appReview.nextReviewerRole().name();
        actions = appReview.actions().stream()
                .map(ActionView::new)
                .collect(Collectors.toList());
        isShared = appReview.isShared();
    }

    public String getAppReviewId() {
        return appReviewId;
    }

    public TravelApplicationView getTravelApplication() {
        return travelApplication;
    }

    public String getNextReviewerRole() {
        return nextReviewerRole;
    }

    public List<ActionView> getActions() {
        return actions;
    }

    @JsonProperty("isShared")
    public boolean isShared() {
        return isShared;
    }

    @Override
    public String getViewType() {
        return "application-review";
    }
}
