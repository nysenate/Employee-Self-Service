package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.model.unit.Address;
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
     * Creates a fully populated route from a partial route entered through the UI.
     * <p>
     * This method should be run after any changes to a route.
     * <p>
     * This populates destination arrival date, departure date, meal per diems, and lodging per diems.
     * Along with the miles and mileage rate for each leg.
     *
     * @param partialRoute
     * @return
     */
    public Route initializeRoute(Route partialRoute) {
        List<Leg> outboundLegs = new ArrayList<>();
        for (int i = 0; i < partialRoute.getOutgoingLegs().size(); i++) {
            Leg leg = createLeg(partialRoute, i);
            outboundLegs.add(leg);
        }

        List<Leg> returnLegs = new ArrayList<>();
        for (int i = partialRoute.getOutgoingLegs().size(); i < partialRoute.getAllLegs().size(); i++) {
            Leg leg = createLeg(partialRoute, i);
            returnLegs.add(leg);
        }

        Route route = new Route(outboundLegs, returnLegs);
        setDestinationPerDiems(route);

        return route;
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
            highestDest.addMealPerDiem(date, highestMealRate);
        }

        // Lodging Rates
        for (Destination dest : route.destinations()) {
            for (LocalDate night : dest.nights()) {
                Dollars lodgingPerDiem = serviceProviderFactory.fetchLodgingRate(night, dest.getAddress());
                dest.addLodgingPerDiem(night, lodgingPerDiem);
            }
        }
    }

    private Set<Destination> destinationsVisitedOn(Route route, LocalDate date) {
        return route.destinations().stream()
                .filter(d -> d.wasVisitedOn(date))
                .collect(Collectors.toSet());
    }

    private Leg createLeg(Route partialRoute, int legIndex) {
        Leg previousLeg = getLegOrNull(partialRoute, legIndex - 1);
        Leg currentLeg = getLegOrNull(partialRoute, legIndex);
        Leg nextLeg = getLegOrNull(partialRoute, legIndex + 1);

        Destination from = createFromDestination(previousLeg, currentLeg);
        Destination to = createToDestination(nextLeg, currentLeg);

        double miles = mileageService.drivingDistance(from.getAddress(), to.getAddress());
        BigDecimal mileageRate = mileageService.getIrsRate(currentLeg.getTravelDate());
        return new Leg(0, from, to, currentLeg.getModeOfTransportation(),
                currentLeg.getTravelDate(), miles, mileageRate);
    }

    private Leg getLegOrNull(Route partialRoute, int legIndex) {
        try {
            return partialRoute.getAllLegs().get(legIndex);
        } catch (Exception ex) {
            return null;
        }
    }

    private Destination createFromDestination(Leg previousLeg, Leg currentLeg) {
        Address address = currentLeg.getFrom().getAddress();
        LocalDate arrival = previousLeg == null ? currentLeg.getTravelDate() : previousLeg.getTravelDate();
        LocalDate departure = currentLeg.getTravelDate();

        return new Destination(currentLeg.getFrom().getAddress(), arrival, departure);
    }

    private Destination createToDestination(Leg nextLeg, Leg currentLeg) {
        Address address = currentLeg.getTo().getAddress();
        LocalDate arrival = currentLeg.getTravelDate();
        LocalDate departure = nextLeg == null ? currentLeg.getTravelDate() : nextLeg.getTravelDate();

        return new Destination(currentLeg.getTo().getAddress(), arrival, departure);
    }

}
