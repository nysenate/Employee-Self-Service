package gov.nysenate.ess.travel.notifications.email;

import gov.nysenate.ess.travel.request.app.TravelApplication;
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

    public TravelAppEmailView(TravelApplication app) {
        appId = String.valueOf(app.getAppId());
        travelerFullName = app.getTraveler().getFullName();
        datesOfTravel = app.startDate().format(DATE_FORMAT);
        if (!app.startDate().equals(app.endDate())) {
            datesOfTravel += " - " + app.endDate().format(DATE_FORMAT);
        }
    }

    public TravelAppEmailView(ApplicationReview appReview) {
        this(appReview.application());
        try {
            Action lastAction = appReview.lastAction();
            if (lastAction.isDisapproval() && appReview.application().getStatus().isDisapproved()) {
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
