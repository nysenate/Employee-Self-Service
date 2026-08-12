package gov.nysenate.ess.travel.review.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.api.application.TravelApplicationSummaryView;
import gov.nysenate.ess.travel.review.ApplicationReview;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationReviewSummaryView implements ViewObject {

    private String appReviewId;
    private TravelApplicationSummaryView application;
    private String pendingReviewerRole;
    private List<ActionView> actions;
    private boolean isShared;

    public ApplicationReviewSummaryView() {
    }

    public ApplicationReviewSummaryView(ApplicationReview appReview) {
        appReviewId = String.valueOf(appReview.getAppReviewId());
        application = new TravelApplicationSummaryView(appReview.application());
        pendingReviewerRole = appReview.pendingReviewerRole().name();
        actions = appReview.actions().stream()
                .map(ActionView::new)
                .collect(Collectors.toList());
        isShared = appReview.isShared();
    }

    public String getAppReviewId() {
        return appReviewId;
    }

    public TravelApplicationSummaryView getApplication() {
        return application;
    }

    public String getPendingReviewerRole() {
        return pendingReviewerRole;
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
        return "Travel Application Review Summary";
    }
}
