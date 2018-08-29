package gov.nysenate.ess.travel.fixtures;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.application.route.ModeOfTransportation;
import gov.nysenate.ess.travel.application.route.Route;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RouteFixture {

    public static BigDecimal MILEAGE_RATE = new BigDecimal("0.545");
    private static UUID id = UUID.randomUUID();

    public static Route longOneDestinationRoute(ModeOfTransportation modeOfTransportation) {
        Leg outboundLeg = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), modeOfTransportation, LocalDate.now());
        Leg returnLeg = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), modeOfTransportation, LocalDate.now());
        return new Route(ImmutableList.of(outboundLeg), ImmutableList.of(returnLeg));
    }

    public static Route longOneDestinationRoute() {
        return longOneDestinationRoute(ModeOfTransportation.PERSONAL_AUTO);
    }

    public static Route shortOneDestinationRoute() {
        Leg outboundLeg = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg returnLeg = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        return new Route(ImmutableList.of(outboundLeg), ImmutableList.of(returnLeg));
    }

    public static Route multiDestinationRoute() {
        Leg outbound1 = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg outbound2 = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg outbound3 = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg returnLeg = new Leg(UUID.randomUUID(), new TravelAddress(id), new TravelAddress(id), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        return new Route(ImmutableList.of(outbound1, outbound2, outbound3), ImmutableList.of(returnLeg));
    }
}
