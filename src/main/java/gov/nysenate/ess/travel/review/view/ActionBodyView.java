package gov.nysenate.ess.travel.review.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.review.controller.ApplicationReviewCtrl;

/**
 * This view is accepted as the body for approve and disapprove api
 * requests in {@link ApplicationReviewCtrl}.
 */
public class ActionBodyView implements ViewObject {

    private String notes;

    public ActionBodyView() {
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String getViewType() {
        return "action body";
    }
}
