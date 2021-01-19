package gov.nysenate.ess.travel.notifications.email;

import gov.nysenate.ess.travel.review.Action;
import gov.nysenate.ess.travel.review.ApplicationReview;

import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

/**
 * A view of a travel app email notification
 */
public class TravelAppEmailView {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("M/d/yyyy");

    private String appId;
    private String travelerFullName;
    private String datesOfTravel;
    private String disapproverFullName;
    private String disapprovalReason;

    public TravelAppEmailView(ApplicationReview appReview) {
        appId = String.valueOf(appReview.application().getAppId());
        travelerFullName = appReview.application().getTraveler().getFullName();
        datesOfTravel = appReview.application().activeAmendment().startDate().format(DATE_FORMAT);
        if (!appReview.application().activeAmendment().startDate().equals(appReview.application().activeAmendment().endDate())) {
            datesOfTravel += " - " + appReview.application().activeAmendment().endDate().format(DATE_FORMAT);
        }

        try {
            Action lastAction = appReview.lastAction();
            if (lastAction.isDisapproval() && appReview.application().status().isDisapproved()) {
                disapproverFullName = appReview.lastAction().user().getFullName();
                disapprovalReason = appReview.lastAction().notes();
            }
        } catch (NoSuchElementException ignored) {}

    }

    public String getAppId() {
        return appId;
    }

    public String getTravelerFullName() {
        return travelerFullName;
    }

    public String getDatesOfTravel() {
        return datesOfTravel;
    }

    public String getDisapproverFullName() {
        return disapproverFullName;
    }

    public String getDisapprovalReason() {
        return disapprovalReason;
    }
}
