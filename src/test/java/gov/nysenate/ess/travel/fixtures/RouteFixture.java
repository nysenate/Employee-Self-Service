package gov.nysenate.ess.travel.fixtures;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.route.Leg;
import gov.nysenate.ess.travel.route.MethodOfTravel;
import gov.nysenate.ess.travel.route.ModeOfTransportation;
import gov.nysenate.ess.travel.route.Route;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RouteFixture {

    public static BigDecimal MILEAGE_RATE = new BigDecimal("0.545");

    public static Route longOneDestinationRoute(ModeOfTransportation modeOfTransportation) {
        Leg outboundLeg = new Leg(new Address(), new Address(), modeOfTransportation, LocalDate.now());
        Leg returnLeg = new Leg(new Address(), new Address(), modeOfTransportation, LocalDate.now());
        return new Route(ImmutableList.of(outboundLeg), ImmutableList.of(returnLeg));
    }

    public static Route longOneDestinationRoute() {
        return longOneDestinationRoute(ModeOfTransportation.PERSONAL_AUTO);
    }

    public static Route shortOneDestinationRoute() {
        Leg outboundLeg = new Leg(new Address(), new Address(), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg returnLeg = new Leg(new Address(), new Address(), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        return new Route(ImmutableList.of(outboundLeg), ImmutableList.of(returnLeg));
    }

    public static Route multiDestinationRoute() {
        Leg outbound1 = new Leg(new Address(), new Address(), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg outbound2 = new Leg(new Address(), new Address(), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg outbound3 = new Leg(new Address(), new Address(), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        Leg returnLeg = new Leg(new Address(), new Address(), ModeOfTransportation.PERSONAL_AUTO, LocalDate.now());
        return new Route(ImmutableList.of(outbound1, outbound2, outbound3), ImmutableList.of(returnLeg));
    }
}
