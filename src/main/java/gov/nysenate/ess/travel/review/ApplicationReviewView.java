package gov.nysenate.ess.travel.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.SimpleTravelApplicationView;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationReviewView implements ViewObject {

    private String appReviewId;
    private SimpleTravelApplicationView travelApplication;
    private String nextReviewerRole;
    private List<ActionView> actions;
    private boolean isDiscussionRequested;

    public ApplicationReviewView() {
    }

    public ApplicationReviewView(ApplicationReview appReview) {
        appReviewId = String.valueOf(appReview.getAppReviewId());
        travelApplication = new SimpleTravelApplicationView(appReview.application());
        nextReviewerRole = appReview.nextReviewerRole().name();
        actions = appReview.actions().stream()
                .map(ActionView::new)
                .collect(Collectors.toList());
        isDiscussionRequested = appReview.isDiscussionRequested();
    }

    public String getAppReviewId() {
        return appReviewId;
    }

    public SimpleTravelApplicationView getTravelApplication() {
        return travelApplication;
    }

    public String getNextReviewerRole() {
        return nextReviewerRole;
    }

    public List<ActionView> getActions() {
        return actions;
    }

    @JsonProperty("isDiscussionRequested")
    public boolean isDiscussionRequested() {
        return isDiscussionRequested;
    }

    @Override
    public String getViewType() {
        return "application-review";
    }
}
