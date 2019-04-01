package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteService;
import gov.nysenate.ess.travel.application.route.RouteView;
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
        TravelApplication previousApp = getTravelApplication(app.getAppId());
        if (!previousApp.getPurposeOfTravel().equals(purposeOfTravel)) {
            app.setPurposeOfTravel(purposeOfTravel);
            saveTravelApplication(app);
        }
    }

    public void updateRoute(TravelApplication app, RouteView routeView) {
        Route partialRoute = routeView.toRoute();
        Route fullRoute = routeService.initializeRoute(partialRoute);
        app.setRoute(fullRoute);
        setAllowancesFromRoute(app.getAllowances(), fullRoute);
        saveTravelApplication(app);
    }

    private void setAllowancesFromRoute(Allowances allowances, Route route) {
        allowances.setMileage(route.mileageExpense());
        allowances.setMeals(route.mealPerDiems().total());
        allowances.setLodging(route.lodgingPerDiems().total());
    }

    public void updateAllowances(TravelApplication app, AllowancesView allowancesView) {
        app.setAllowances(allowancesView.toAllowances());
        saveTravelApplication(app);
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
        saveTravelApplication(app);
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


}
