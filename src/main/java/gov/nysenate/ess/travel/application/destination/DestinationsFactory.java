package gov.nysenate.ess.travel.application.destination;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.Route;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DestinationsFactory {

    /**
     * Creates Destinations from a given Route
     * @param route
     * @return
     */
    public Destinations createDestinations(Route route) {
        List<Destination> destinations = new ArrayList<>();

        ImmutableList<Leg> outboundLegs = route.getOutgoingLegs();
        ImmutableList<Leg> returnLegs = route.getReturnLegs();
        for (int i = 0; i < route.getOutgoingLegs().size(); i++) {
            destinations.add(createDestination(outboundLegs, returnLegs, i));
        }

        return new Destinations(destinations);
    }

    private Destination createDestination(ImmutableList<Leg> outboundLegs, ImmutableList<Leg> returnLegs, int i) {
        TravelAddress address = outboundLegs.get(i).getTo();
        LocalDate arrivalDate = outboundLegs.get(i).getTravelDate();
        LocalDate departureDate = calculateDepartureDate(outboundLegs, returnLegs, i);
        return new Destination(UUID.randomUUID(), address, arrivalDate, departureDate);
    }

    private LocalDate calculateDepartureDate(List<Leg> outboundLegs, List<Leg> returnLegs, int i) {
        LocalDate departureDate;
        if (lastOutboundSegment(outboundLegs, i)) {
            departureDate = returnLegs.get(0).getTravelDate();
        }
        else {
            departureDate = outboundLegs.get(i + 1).getTravelDate();
        }
        return departureDate;
    }

    private boolean lastOutboundSegment(List<Leg> outboundLegs, int i) {
        return i == outboundLegs.size() - 1;
    }
}
