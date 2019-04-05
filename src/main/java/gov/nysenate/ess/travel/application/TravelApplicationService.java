package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.route.*;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TravelApplicationService {

    @Autowired private TravelApplicationDao applicationDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RouteService routeService;


    public void updatePurposeOfTravel(TravelApplication app, String purposeOfTravel) {
        app.setPurposeOfTravel(purposeOfTravel);
    }

    public void updateRoute(TravelApplication app, SimpleRouteView simpleRouteView) {
        Route fullRoute = routeService.createRoute(simpleRouteView);
        app.setRoute(fullRoute);
    }

    public void updateAllowances(TravelApplication app, AllowancesView allowancesView) {
        app.setAllowances(allowancesView.toAllowances());
    }

    /**
     * Save changes to a Travel Application.
     *
     * @param app
     * @return
     */
    public TravelApplication saveTravelApplication(TravelApplication app) {
        app.setModifiedDateTime(LocalDateTime.now());
        app.setModifiedBy(app.getTraveler());
        applicationDao.insertTravelApplication(app);
        return app;
    }

    public void deleteTravelApplication(int appId) {
        applicationDao.deleteTravelApplication(appId);
    }

    /**
     * Gets the TravelApplication currently being filled out by the given traveler.
     * If none, create a new empty application.
     *
     * @param travelerId
     * @return
     */
    public TravelApplication uncompleteAppForTraveler(int travelerId) {
        List<TravelApplication> apps = applicationDao.selectTravelApplications(travelerId);
        Optional<TravelApplication> appOptional = apps.stream()
                .filter(app -> app.getSubmittedDateTime() == null)
                .findFirst();
        if (appOptional.isPresent()) {
            return appOptional.get();
        } else {
            TravelApplication app = new TravelApplication(0, 0, employeeInfoService.getEmployee(travelerId));
            saveTravelApplication(app);
            return app;
        }
    }

    /**
     * Submits a TravelApplication.
     *
     * @param app
     * @return
     */
    public TravelApplication submitTravelApplication(TravelApplication app) {
        app.setSubmittedDateTime(LocalDateTime.now());
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
                    leg.setPerDiem(new PerDiem(legView.date(), legView.rate(), legView.isReimbursementRequested()));
                }
            }
        }
    }
}
