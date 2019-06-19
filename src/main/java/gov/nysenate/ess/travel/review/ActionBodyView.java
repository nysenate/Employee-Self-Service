package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.core.client.view.base.ViewObject;

/**
 * This view is accepted as the body for approve and disapprove api
 * requests in {@link ApplicationReviewCtrl}.
 */
public class ActionBodyView implements ViewObject {

    private String notes;
    private boolean isDiscussionRequested;

    public ActionBodyView() {
    }

    public String getNotes() {
        return notes;
    }

    public boolean isDiscussionRequested() {
        return isDiscussionRequested;
    }

    @Override
    public String getViewType() {
        return "action body";
    }
}
