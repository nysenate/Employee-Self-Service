package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelApplicationService {

    @Autowired private TravelApplicationDao applicationDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RouteService routeService;

    /**
     * Inserts a travel application into the backing store.
     *
     * @param app
     * @return
     */
    public TravelApplication insertTravelApplication(TravelApplication app) {
        LocalDateTime modifiedDateTime = LocalDateTime.now();
        app.setSubmittedDateTime(modifiedDateTime);
        app.setModifiedDateTime(modifiedDateTime);
        app.setModifiedBy(app.getTraveler());
        applicationDao.insertTravelApplication(app);
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
        return applicationDao.selectTravelApplications(travelerId);
    }

    public void setAllowancesFromRoute(Allowances allowances, Route route) {
        allowances.setMileage(route.mileageExpense());
        allowances.setMeals(route.mealPerDiems().total());
        allowances.setLodging(route.lodgingPerDiems().total());
    }


    public Route initializeRoute(Route partialRoute) {
        return routeService.initializeRoute(partialRoute);
    }

}
