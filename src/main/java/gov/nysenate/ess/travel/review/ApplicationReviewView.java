package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.SimpleTravelApplicationView;

import java.util.List;
import java.util.stream.Collectors;

public class ApplicationReviewView implements ViewObject {

    private String appReviewId;
    private SimpleTravelApplicationView travelApplication;
    private List<ActionView> actions;

    public ApplicationReviewView() {
    }

    public ApplicationReviewView(ApplicationReview appReview) {
        appReviewId = String.valueOf(appReview.getAppReviewId());
        travelApplication = new SimpleTravelApplicationView(appReview.application());
        actions = appReview.actions().stream()
                .map(ActionView::new)
                .collect(Collectors.toList());
    }

    public String getAppReviewId() {
        return appReviewId;
    }

    public SimpleTravelApplicationView getTravelApplication() {
        return travelApplication;
    }

    public List<ActionView> getActions() {
        return actions;
    }

    @Override
    public String getViewType() {
        return "application-review";
    }
}
