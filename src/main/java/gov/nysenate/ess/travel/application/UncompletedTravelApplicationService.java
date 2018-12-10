package gov.nysenate.ess.travel.application;

import com.google.common.collect.Lists;
import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowances;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesFactory;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowances;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesFactory;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowanceFactory;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowances;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.application.destination.DestinationsFactory;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class UncompletedTravelApplicationService {

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private TravelApplicationService applicationService;
    @Autowired private UncompletedTravelApplicationDao uncompletedAppDao;
    @Autowired private DestinationsFactory destinationsFactory;
    @Autowired private MileageAllowanceFactory mileageAllowanceFactory;
    @Autowired private MealAllowancesFactory mealAllowancesFactory;
    @Autowired private LodgingAllowancesFactory lodgingAllowancesFactory;

    /**
     * Gets the current Uncompleted travel application for an employee
     * or creates a new application if non currently exist.
     *
     * @param travelerId
     * @param submitterId
     * @return
     */
    public TravelApplication getSavedTravelApplication(int travelerId, int submitterId) {
        Employee traveler = employeeInfoService.getEmployee(travelerId);
        Employee submitter = employeeInfoService.getEmployee(submitterId);

        TravelApplication app;
        if (uncompletedAppDao.hasUncompletedApplication(traveler.getEmployeeId())) {
            app = uncompletedAppDao.selectUncompletedApplication(traveler.getEmployeeId());
        } else {
            app = new TravelApplication(UUID.randomUUID(), UUID.randomUUID(), traveler, submitter);
            uncompletedAppDao.saveUncompletedApplication(app);
        }
        return app;
    }

    public TravelApplication savePurpose(UUID appId, String purpose) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app.setPurposeOfTravel(purpose);
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Updates an application route's outbound legs, keeping the return legs the same.
     * If these outbound legs are different than they were previously, we need to
     * clear the destinations and allowances since they will no longer be valid.
     */
    public TravelApplication saveOutboundLegs(UUID appId, List<Leg> outboundLegs) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app = addOutboundLegs(app, outboundLegs);
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    private TravelApplication addOutboundLegs(TravelApplication app, List<Leg> outboundLegs) {
        List<Leg> previousLegs = app.getRoute().getOutgoingLegs();
        if (!previousLegs.equals(outboundLegs)) {
            resetDestinations(app);
            resetDerivedAllowances(app);
        }
        app.setRoute(new Route(outboundLegs, app.getRoute().getReturnLegs()));
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Updates the return legs of an applications route.
     * Clears the applications destinations and derived allowances if these legs have been modified.
     * <p>
     * Also calculates the destinations and derived allowances if they are missing.
     *
     * @throws ProviderException if an error is encountered while communicating with our mileage, meal rate, or lodging rate providers.
     */
    public TravelApplication saveReturnLegs(UUID appId, List<Leg> returnLegs) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app = addReturnLegs(app, returnLegs);

        // Calculate destinations, mileage, meal, and lodging allowances from the now complete Route.
        if (app.getDestinations().size() == 0) {
            app.setDestinations(destinationsFactory.createDestinations(app.getRoute()));
            app.setMileageAllowances(mileageAllowanceFactory.calculateMileageAllowance(app.getRoute()));
            app.setMealAllowances(mealAllowancesFactory.createMealAllowances(app.getDestinations()));
            app.setLodgingAllowances(lodgingAllowancesFactory.createLodgingAllowances(app.getDestinations()));
        }
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    private TravelApplication addReturnLegs(TravelApplication app, List<Leg> returnLegs) {
        List<Leg> previousLegs = app.getRoute().getReturnLegs();
        if (!previousLegs.equals(returnLegs)) {
            resetDestinations(app);
            resetDerivedAllowances(app);
        }
        app.setRoute(new Route(app.getRoute().getOutgoingLegs(), returnLegs));
        return app;
    }

    private void resetDestinations(TravelApplication app) {
        app.setDestinations(new Destinations(Lists.newArrayList()));
    }

    private void resetDerivedAllowances(TravelApplication app) {
        app.setMileageAllowances(new MileageAllowances(Lists.newArrayList(), Lists.newArrayList()));
        app.setMealAllowances(new MealAllowances(Lists.newArrayList()));
        app.setLodgingAllowances(new LodgingAllowances(Lists.newArrayList()));
    }

    /**
     * Set expenses estimated by the traveler.
     */
    public TravelApplication saveExpenses(UUID appId, Dollars tolls, Dollars parking, Dollars alternate, Dollars registartion, Dollars trainAndPlane) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app.setTolls(tolls);
        app.setParking(parking);
        app.setAlternate(alternate);
        app.setRegistration(registartion);
        app.setTrainAndAirplane(trainAndPlane);

        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Updates meal allowances. Main purpose is to update the is_meals_requested flag in each allowance.
     */
    public TravelApplication updateMealAllowances(UUID appId, MealAllowances mealAllowances) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app.setMealAllowances(mealAllowances);
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Updates an applications lodging allowances.
     * Main purpose is to update the is_lodging_requested flag in each lodging allowance.
     */
    public TravelApplication updateLodgingAllowances(UUID appId, LodgingAllowances lodgingAllowances) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app.setLodgingAllowances(lodgingAllowances);
        uncompletedAppDao.saveUncompletedApplication(app);
        return app;
    }

    /**
     * Submit a travel application
     */
    public TravelApplication submitApplication(UUID appId) {
        TravelApplication app = uncompletedAppDao.selectUncompletedApplication(appId);
        app = applicationService.insertTravelApplication(app);
        uncompletedAppDao.deleteUncompletedApplication(app.getTraveler().getEmployeeId());
        return app;
    }

    /**
     * Deletes the UncompletedTravelApplication for the given employee.
     *
     * @param empId
     */
    public void deleteUncompletedTravelApplication(int empId) {
        uncompletedAppDao.deleteUncompletedApplication(empId);
    }
}
