package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelApplicationService {

    @Autowired private TravelApplicationDao applicationDao;
    @Autowired private ApplicationReviewService appReviewService;

    /**
     * Save changes to a Travel Application.
     *
     * @param app
     * @param saver The employee who is saving the application.
     * @return
     */
    public TravelApplication saveTravelApplication(TravelApplication app, Employee saver) {
        // FIXME these should update only the amendment being saved.
        app.activeAmendment().setCreatedDateTime(LocalDateTime.now());
        app.activeAmendment().setCreatedBy(saver);
        applicationDao.saveTravelApplication(app);
        return app;
    }

    /**
     * Creates and saves a new ApplicationApproval for this TravelApplication.
     *
     * @param app
     * @param submitter The employee submitting this application.
     * @return
     */
    public TravelApplication submitTravelApplication(TravelApplication app, Employee submitter) {
        saveTravelApplication(app, submitter);

        // Don't create application reviews for v1.0 release
//        ApplicationReview appReview = appReviewService.createApplicationReview(app);
//        appReviewService.saveApplicationReview(appReview);
        return app;
    }

    /**
     * Get Travel application by application id
     *
     * @return
     */
    public TravelApplication getTravelApplication(int appId) {
        return applicationDao.selectTravelApplication(appId);
    }

    /**
     * Get a list of an employees travel applications this user has submitted or is the traveler.
     *
     * @return
     */
    public List<TravelApplication> selectTravelApplications(int userId) {
        return applicationDao.selectTravelApplications(userId).stream()
                .filter(app -> app.getSubmittedDateTime() != null)
                .collect(Collectors.toList());
    }

    public void updateMileagePerDiems(TravelApplication app, MileagePerDiems mileagePerDiem) {
        for (Leg qualifyingLeg : mileagePerDiem.qualifyingLegs()) {
            for (Leg appLeg : app.activeAmendment().route().getAllLegs()) {
                if (appLeg.fromAddress().equals(qualifyingLeg.fromAddress())
                        && appLeg.toAddress().equals(qualifyingLeg.toAddress())
                        && appLeg.travelDate().equals(qualifyingLeg.travelDate())) {
                    appLeg.setIsReimbursementRequested(qualifyingLeg.isReimbursementRequested());
                }
            }
        }
    }
}
