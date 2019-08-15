package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverridesView;
import gov.nysenate.ess.travel.application.route.*;
import gov.nysenate.ess.travel.application.route.destination.Destination;
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
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RouteService routeService;
    @Autowired private ApplicationReviewService appReviewService;


    public void updatePurposeOfTravel(TravelApplication app, String purposeOfTravel) {
        app.setPurposeOfTravel(purposeOfTravel);
    }

    public void updateRoute(TravelApplication app, RouteView routeView) {
        Route fullRoute = routeService.createRoute(routeView.toRoute());
        app.setRoute(fullRoute);
    }

    public void updateAllowances(TravelApplication app, AllowancesView allowancesView) {
        app.setAllowances(allowancesView.toAllowances());
    }

    public void updatePerDiemOverrides(TravelApplication app, PerDiemOverridesView overridesView) {
        app.setPerDiemOverrides(overridesView.toPerDiemOverrides());
    }

    /**
     * Save changes to a Travel Application.
     *
     * @param app
     * @param saver The employee who is saving the application.
     * @return
     */
    public TravelApplication saveTravelApplication(TravelApplication app, Employee saver) {
        app.setModifiedDateTime(LocalDateTime.now());
        app.setModifiedBy(saver);
        applicationDao.insertTravelApplication(app);
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
        app.setSubmittedDateTime(LocalDateTime.now());

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
     * Get a list of an employees travel applications.
     *
     * @return
     */
    public List<TravelApplication> selectTravelApplications(int travelerId) {
        return applicationDao.selectTravelApplications(travelerId).stream()
                .filter(app -> app.getSubmittedDateTime() != null)
                .collect(Collectors.toList());
    }

    public void updateMealPerDiems(TravelApplication app, MealPerDiemsView mealPerDiemsView) {
        for (MealPerDiemView perDiemView : mealPerDiemsView.getAllMealPerDiems()) {
            for (Destination dest : app.getRoute().destinations()) {
                if (dest.getAddress().equals(perDiemView.getAddress().toAddress())) {
                    dest.addMealPerDiem(new PerDiem(perDiemView.date(), perDiemView.rate(), perDiemView.isReimbursementRequested()));
                }
            }
        }
    }

    public void updateLodgingPerDiems(TravelApplication app, LodgingPerDiemsView lodgingPerDiemsView) {
        for (LodgingPerDiemView perDiemView : lodgingPerDiemsView.getAllLodgingPerDiems()) {
            for (Destination dest : app.getRoute().destinations()) {
                if (dest.getAddress().equals(perDiemView.getAddress().toAddress())) {
                    dest.addLodgingPerDiem(new PerDiem(perDiemView.date(), perDiemView.rate(), perDiemView.isReimbursementRequested()));
                }
            }
        }
    }

    public void updateMileagePerDiems(TravelApplication app, MileagePerDiemsView mileagePerDiemView) {
        for (LegView legView : mileagePerDiemView.getQualifyingLegs()) {
            for (Leg leg : app.getRoute().getAllLegs()) {
                if (leg.fromAddress().equals(legView.fromAddress())
                        && leg.toAddress().equals(legView.toAddress())
                        && leg.travelDate().equals(legView.date())) {
                    leg.setPerDiem(new PerDiem(legView.date(), legView.mileageRate(), legView.isReimbursementRequested()));
                }
            }
        }
    }
}
