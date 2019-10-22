package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMie;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public Route createRoute(Route route) throws IOException {
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

        Route fullRoute = new Route(outboundLegs, returnLegs);
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

        double miles = mileageService.drivingDistance(from.getAddress(), to.getAddress());
        BigDecimal mileageRate = mileageService.getIrsRate(currentLeg.travelDate());
        PerDiem perDiem = new PerDiem(currentLeg.travelDate(), mileageRate);
        return new Leg(0, from, to,
                new ModeOfTransportation(currentLeg.methodOfTravel(), currentLeg.methodOfTravelDescription()),
                miles, perDiem, isOutbound, true);
    }

    private Leg getLegOrNull(Route simpleRoute, int legIndex) {
        try {
            return simpleRoute.getAllLegs().get(legIndex);
        } catch (Exception ex) {
            return null;
        }
    }

    private Destination createFromDestination(Leg previousLeg, Leg currentLeg) {
        GoogleAddress address = currentLeg.fromAddress();
        LocalDate arrival = previousLeg == null ? currentLeg.travelDate() : previousLeg.travelDate();
        LocalDate departure = currentLeg.travelDate();

        return new Destination(address, arrival, departure);
    }

    private Destination createToDestination(Leg nextLeg, Leg currentLeg) {
        GoogleAddress address = currentLeg.toAddress();
        LocalDate arrival = currentLeg.travelDate();
        LocalDate departure = nextLeg == null ? currentLeg.travelDate() : nextLeg.travelDate();

        return new Destination(address, arrival, departure);
    }

    private void setDestinationPerDiems(Route route) throws IOException {
        for (Destination d : route.destinations()) {
            // Meal Rates
            for (LocalDate date : d.days()) {
                GsaMie mie = gsaAllowanceService.fetchGsaMie(date, d.getAddress());
                d.addGsaMie(date, mie);
            }

            // Lodging Rates
            for (LocalDate night : d.nights()) {
                Dollars lodgingRate = gsaAllowanceService.fetchLodgingRate(night, d.getAddress());
                PerDiem lodgingPerDiem = new PerDiem(night, lodgingRate);
                d.addLodgingPerDiem(lodgingPerDiem);
            }
        }
    }
}
