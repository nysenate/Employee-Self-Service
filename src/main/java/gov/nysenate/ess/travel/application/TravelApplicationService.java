package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.application.unsubmitted.UnsubmittedAppDao;
import gov.nysenate.ess.travel.review.ApplicationReview;
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
//        app.activeAmendment().setCreatedDateTime(LocalDateTime.now());
//        app.activeAmendment().setCreatedBy(saver);
//        applicationDao.saveTravelApplication(app);
        return app;
    }

    /**
     * Creates and saves a new TravelApplication with one amendment {@code amd}.
     * This also creates and saves an ApplicationReview.
     *
     * @param amd The first amendment to the TravelApplication
     * @param traveler The employee who will be traveling.
     * @param submitter The employee who is submitting the application.
     * @return
     */
    public TravelApplication submitTravelApplication(Amendment amd, Employee traveler, Employee submitter) {
        amd = new Amendment.Builder(amd)
                .withAmendmentId(0)
                .withVersion(Version.A)
                .withCreatedBy(submitter)
                .withCreatedDateTime(LocalDateTime.now())
                .build();

        TravelApplication app = new TravelApplication(traveler, amd);
        saveTravelApplication(app, submitter);
        applicationDao.saveTravelApplication(app);

        ApplicationReview appReview = appReviewService.createApplicationReview(app);
        appReviewService.saveApplicationReview(appReview);
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
}
