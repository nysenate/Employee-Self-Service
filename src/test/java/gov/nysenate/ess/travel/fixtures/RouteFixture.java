package gov.nysenate.ess.travel.fixtures;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.route.Leg;
import gov.nysenate.ess.travel.route.ModeOfTransportation;
import gov.nysenate.ess.travel.route.Route;

public class RouteFixture {

    private static Dollars MILEAGE_RATE = new Dollars("0.50");

    public static Route longOneDestinationRoute(ModeOfTransportation modeOfTransportation,
                                                boolean isRequestingMileage) {
        Leg outboundLeg = new Leg(new Address(), new Address(), 200, modeOfTransportation);
        Leg returnLeg = new Leg(new Address(), new Address(), 200, modeOfTransportation);
        return new Route(ImmutableList.of(outboundLeg), ImmutableList.of(returnLeg),
                MILEAGE_RATE, isRequestingMileage);
    }


    public static Route longOneDestinationRoute() {
        return longOneDestinationRoute(ModeOfTransportation.PERSONAL_AUTO, true);
    }

    public static Route shortOneDestinationRoute() {
        Leg outboundLeg = new Leg(new Address(), new Address(), 34, ModeOfTransportation.PERSONAL_AUTO);
        Leg returnLeg = new Leg(new Address(), new Address(), 34, ModeOfTransportation.PERSONAL_AUTO);
        return new Route(ImmutableList.of(outboundLeg), ImmutableList.of(returnLeg), MILEAGE_RATE, true);
    }

    public static Route multiDestinationRoute() {
        Leg outbound1 = new Leg(new Address(), new Address(), 13, ModeOfTransportation.PERSONAL_AUTO);
        Leg outbound2 = new Leg(new Address(), new Address(), 10, ModeOfTransportation.PERSONAL_AUTO);
        Leg outbound3 = new Leg(new Address(), new Address(), 27, ModeOfTransportation.PERSONAL_AUTO);
        Leg returnLeg = new Leg(new Address(), new Address(), 25, ModeOfTransportation.PERSONAL_AUTO);
        return new Route(ImmutableList.of(outbound1, outbound2, outbound3), ImmutableList.of(returnLeg),
                MILEAGE_RATE, true);
    }
}
