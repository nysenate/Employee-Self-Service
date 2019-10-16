package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.provider.ServiceProviderFactory;
import gov.nysenate.ess.travel.provider.miles.MileageAllowanceService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired private ServiceProviderFactory serviceProviderFactory;
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
    public Route createRoute(Route route) {
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
        PerDiem perDiem = new PerDiem(currentLeg.travelDate(), mileageRate, true);
        return new Leg(0, from, to,
                new ModeOfTransportation(currentLeg.methodOfTravel(), currentLeg.methodOfTravelDescription()),
                miles, perDiem, isOutbound);
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

    private void setDestinationPerDiems(Route route) {
        LocalDate start = route.startDate();
        LocalDate end = route.endDate().plusDays(1);

        // Meal Rates
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            Set<Destination> dests = destinationsVisitedOn(route, date);
            Map<Destination, Dollars> destToMealExpense = new HashMap<>();
            for (Destination dest : dests) {
                destToMealExpense.put(dest, serviceProviderFactory.fetchMealRate(date, dest.getAddress()));
            }
            Destination highestDest = null;
            Dollars highestMealRate = null;
            for (Map.Entry<Destination, Dollars> entry : destToMealExpense.entrySet()) {
                if (highestMealRate == null || entry.getValue().compareTo(highestMealRate) > 0) {
                    highestMealRate = entry.getValue();
                    highestDest = entry.getKey();
                }
            }
            PerDiem pd = new PerDiem(date, highestMealRate, true);
            highestDest.addMealPerDiem(pd);
        }

        // Lodging Rates
        for (Destination dest : route.destinations()) {
            for (LocalDate night : dest.nights()) {
                Dollars lodgingPerDiem = serviceProviderFactory.fetchLodgingRate(night, dest.getAddress());
                PerDiem pd = new PerDiem(night, lodgingPerDiem, true);
                dest.addLodgingPerDiem(pd);
            }
        }
    }
}
