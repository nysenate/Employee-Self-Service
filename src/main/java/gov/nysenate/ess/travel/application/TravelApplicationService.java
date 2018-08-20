package gov.nysenate.ess.travel.application;

import com.google.common.collect.Lists;
import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowanceFactory;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowances;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowances;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesFactory;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowances;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesFactory;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.application.destination.DestinationsFactory;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TravelApplicationService {

    @Autowired private InMemoryTravelAppDao appDao;
    @Autowired private TravelApplicationDao applicationDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private DestinationsFactory destinationsFactory;
    @Autowired private MileageAllowanceFactory mileageAllowanceFactory;
    @Autowired private MealAllowancesFactory mealAllowancesFactory;
    @Autowired private LodgingAllowancesFactory lodgingAllowancesFactory;

    /**
     * Creates a new travel application
     * @return
     */
    public TravelApplication initTravelApplication(Employee traveler, Employee submitter) {
        return new TravelApplication(UUID.randomUUID(), UUID.randomUUID(), traveler, submitter);
    }

    public TravelApplication insertTravelApplication(TravelApplication app) {
        LocalDateTime modifiedDateTime = LocalDateTime.now();
        app.setSubmittedDateTime(modifiedDateTime);
        app.setModifiedDateTime(modifiedDateTime);
        app.setModifiedBy(app.getSubmitter()); // submitter is also the first modifier.
        applicationDao.insertTravelApplication(app);
        return app;
    }

    // TODO Pessimistic locking?
    public TravelApplication updateTravelApplication(TravelApplication app, int modifiedByEmpId) {
        LocalDateTime modifiedDateTime = LocalDateTime.now();
        app.setModifiedDateTime(modifiedDateTime);
        app.setModifiedBy(employeeInfoService.getEmployee(modifiedByEmpId));
        app.setVersionId(UUID.randomUUID());
        return null;
    }

    public TravelApplication addOutboundLegs(UUID appId, List<Leg> outboundLegs) {
        TravelApplication app = appDao.getUncompletedAppById(appId).toTravelApplication();
        List<Leg> previousLegs = app.getRoute().getOutgoingLegs();
        if (!previousLegs.equals(outboundLegs)) {
            resetDestinations(app);
            resetDerivedAllowances(app);
        }
        app.setRoute(new Route(outboundLegs, app.getRoute().getReturnLegs()));
        appDao.saveUncompleteTravelApp(new TravelApplicationView(app));
        return app;
    }

    public TravelApplication addReturnLegs(UUID appId, List<Leg> returnLegs) throws InterruptedException, ApiException, IOException {
        TravelApplication app = appDao.getUncompletedAppById(appId).toTravelApplication();
        List<Leg> previousLegs = app.getRoute().getReturnLegs();
        if (!previousLegs.equals(returnLegs)) {
            resetDestinations(app);
            resetDerivedAllowances(app);
        }
        app.setRoute(new Route(app.getRoute().getOutgoingLegs(), returnLegs));
        appDao.saveUncompleteTravelApp(new TravelApplicationView(app));

        // Calculate destinations, mileage, meal, and lodging allowances from Route.
        if (app.getDestinations().size() == 0) {
            app.setDestinations(calculateDestinations(app.getRoute()));
            app.setMileageAllowances(calculateMileageAllowances(app.getRoute()));
            app.setMealAllowances(calculateMealAllowances(app.getDestinations()));
            app.setLodgingAllowances(calculateLodgingAllowances(app.getDestinations()));
        }
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

    private Destinations calculateDestinations(Route route) {
        return destinationsFactory.createDestinations(route);
    }

    private MileageAllowances calculateMileageAllowances(Route route) throws InterruptedException, ApiException, IOException {
        return mileageAllowanceFactory.calculateMileageAllowance(route);
    }

    private MealAllowances calculateMealAllowances(Destinations dests) throws IOException {
        return mealAllowancesFactory.createMealAllowances(dests);
    }

    private LodgingAllowances calculateLodgingAllowances(Destinations dests) throws IOException {
        return lodgingAllowancesFactory.createLodgingAllowances(dests);
    }

}
