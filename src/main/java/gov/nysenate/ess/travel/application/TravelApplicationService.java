package gov.nysenate.ess.travel.application;

import com.google.common.collect.Lists;
import com.google.maps.errors.ApiException;
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
import java.util.List;

@Service
public class TravelApplicationService {

    @Autowired private InMemoryTravelAppDao appDao;
    @Autowired private DestinationsFactory destinationsFactory;
    @Autowired private MileageAllowanceFactory mileageAllowanceFactory;
    @Autowired private MealAllowancesFactory mealAllowancesFactory;
    @Autowired private LodgingAllowancesFactory lodgingAllowancesFactory;

    public TravelApplication addOutboundLegs(int appId, List<Leg> outboundLegs) {
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

    public TravelApplication addReturnLegs(long appId, List<Leg> returnLegs) throws InterruptedException, ApiException, IOException {
        TravelApplication app = appDao.getUncompletedAppById((int)appId).toTravelApplication();
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
