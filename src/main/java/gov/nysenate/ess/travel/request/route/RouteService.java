package gov.nysenate.ess.travel.request.route;

import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired private GsaAllowanceService gsaAllowanceService;
    @Autowired private MileageAllowanceService mileageService;

    /**
     * Creates a fully populated route from a SimpleRouteView entered through the UI.
     * <p>
     * This method should be run after any changes to a route.
     * <p>
     * This populates destination arrival date, departure date, meal per diems, and lodging per diems.
     * Along with the miles and mileage rate for each leg.
     *
     * @return
     */
    public Route createRoute(Route route) throws ProviderException {
        List<Leg> outboundLegs = new ArrayList<>();
        for (int i = 0; i < route.getOutboundLegs().size(); i++) {
            Leg leg = createLeg(route, i, true);
            outboundLegs.add(leg);
        }

        List<Leg> returnLegs = new ArrayList<>();
        for (int i = route.getOutboundLegs().size(); i < route.getAllLegs().size(); i++) {
            Leg leg = createLeg(route, i, false);
            returnLegs.add(leg);
        }

        Route fullRoute = new Route(outboundLegs, returnLegs,
                route.firstLegQualifiesForBreakfast(), route.lastLegQualifiesForDinner());
        setDestinationPerDiems(fullRoute);

        return fullRoute;
    }

    // Set per diems on each destination. If a day has multiple per diems only use the highest one.
    private Set<Destination> destinationsVisitedOn(Route route, LocalDate date) {
        return route.destinations().stream()
                .filter(d -> d.wasVisitedOn(date))
                .collect(Collectors.toSet());
    }

    private Leg createLeg(Route simpleRoute, int legIndex, boolean isOutbound) {
        Leg previousLeg = getLegOrNull(simpleRoute, legIndex - 1);
        Leg currentLeg = getLegOrNull(simpleRoute, legIndex);
        Leg nextLeg = getLegOrNull(simpleRoute, legIndex + 1);

        Destination from = createFromDestination(previousLeg, currentLeg);
        Destination to = createToDestination(nextLeg, currentLeg);

        return new Leg(0, from, to,
                new ModeOfTransportation(currentLeg.methodOfTravel(), currentLeg.methodOfTravelDescription()),
                isOutbound, currentLeg.travelDate());
    }

    private Leg getLegOrNull(Route simpleRoute, int legIndex) {
        try {
            return simpleRoute.getAllLegs().get(legIndex);
        } catch (Exception ex) {
            return null;
        }
    }

    private Destination createFromDestination(Leg previousLeg, Leg currentLeg) {
        TravelAddress address = currentLeg.fromAddress();
        LocalDate arrival = previousLeg == null ? currentLeg.travelDate() : previousLeg.travelDate();
        LocalDate departure = currentLeg.travelDate();

        return new Destination(address, arrival, departure);
    }

    private Destination createToDestination(Leg nextLeg, Leg currentLeg) {
        TravelAddress address = currentLeg.toAddress();
        LocalDate arrival = currentLeg.travelDate();
        LocalDate departure = nextLeg == null ? currentLeg.travelDate() : nextLeg.travelDate();

        return new Destination(address, arrival, departure);
    }

    private void setDestinationPerDiems(Route route) throws ProviderException {
        for (Destination d : route.destinations()) {
            // Meal Rates
            for (LocalDate date : d.days()) {
                Dollars mealRate = gsaAllowanceService.fetchMealRate(date, d.getAddress().getZip5());
                PerDiem mealPerDiem = new PerDiem(date, mealRate);
                d.addMealPerDiem(mealPerDiem);
            }

            // Lodging Rates
            for (LocalDate night : d.nights()) {
                Dollars lodgingRate = gsaAllowanceService.fetchLodgingRate(night, d.getAddress().getZip5());
                PerDiem lodgingPerDiem = new PerDiem(night, lodgingRate);
                d.addLodgingPerDiem(lodgingPerDiem);
            }
        }
    }
}
