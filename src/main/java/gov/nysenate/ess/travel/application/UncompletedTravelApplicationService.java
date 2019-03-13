package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UncompletedTravelApplicationService {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelApplicationService applicationService;
    @Autowired private UncompletedTravelApplicationDao uncompletedAppDao;

    /**
     * Gets the current Uncompleted travel application for an employee
     * or creates a new application if non currently exist.
     *
     * @param travelerId
     * @return
     */
    public TravelApplication getSavedTravelApplication(int travelerId) {
        Employee traveler = employeeInfoService.getEmployee(travelerId);

        TravelApplication app;
        if (uncompletedAppDao.hasUncompletedApplication(traveler.getEmployeeId())) {
            app = uncompletedAppDao.selectUncompletedApplication(traveler.getEmployeeId());
        } else {
            app = new TravelApplication(0, 0, traveler);
            uncompletedAppDao.saveUncompletedApplication(app);
        }
        return app;
    }

    public TravelApplication savePurpose(int travelerId, String purpose) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(travelerId);
        app.setPurposeOfTravel(purpose);
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Updates an application route's outbound legs, keeping the return legs the same.
     */
    public TravelApplication saveOutboundLegs(int travelerId, List<Leg> outboundLegs) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(travelerId);
        app.setRoute(new Route(outboundLegs, app.getRoute().getReturnLegs()));
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    public TravelApplication saveRoute(int travelerId, Route partialRoute) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(travelerId);
        Route newRoute = applicationService.initializeRoute(partialRoute);
        app.setRoute(newRoute);

        // Also update allowances derived from the Route
        applicationService.setAllowancesFromRoute(app.getAllowances(), newRoute);

        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Set allowances estimated by the traveler.
     */
    public TravelApplication saveExpenses(int travelerId, Allowances allowances) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(travelerId);
        app.setAllowances(allowances);
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Submit a travel application
     */
    // TODO Make Transactional
    public TravelApplication submitApplication(int travelerId) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(travelerId);
        app = applicationService.insertTravelApplication(app);
        uncompletedAppDao.deleteUncompletedApplication(travelerId);
        return app;
    }

    /**
     * Deletes the UncompletedTravelApplication with the given id.
     */
    public void deleteUncompletedTravelApplication(int travelerId) {
        uncompletedAppDao.deleteUncompletedApplication(travelerId);
    }
}
