package gov.nysenate.ess.travel.unit.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.Dollars;
import gov.nysenate.ess.travel.route.Leg;
import gov.nysenate.ess.travel.route.ModeOfTransportation;
import gov.nysenate.ess.travel.route.Route;
import gov.nysenate.ess.travel.fixtures.RouteFixture;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class RouteTest {

    private static Dollars MILEAGE_RATE = new Dollars("0.50");

    @Test
    public void longTripGetsMileageAllowance() {
        Route route = RouteFixture.longOneDestinationRoute();
        assertEquals(new Dollars("200.00"), route.mileageAllowance());
    }

    @Test
    public void outboundTrimLessThan35MilesNoAllowance() {
        Route route = RouteFixture.shortOneDestinationRoute();
        assertEquals(new Dollars("0"), route.mileageAllowance());
    }

    @Test
    public void testMultiDestinationRoute() {
        Route route = RouteFixture.multiDestinationRoute();
        assertEquals(new Dollars("37.50"), route.mileageAllowance());
    }

    @Test
    public void mileageAllowanceRequiresPersonalAuto() {
        Route route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.SENATE_VEHICLE, true);
        assertEquals(new Dollars("0"), route.mileageAllowance());

        route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.OTHER, true);
        assertEquals(new Dollars("0"), route.mileageAllowance());

        route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.AIRPLANE, true);
        assertEquals(new Dollars("0"), route.mileageAllowance());

        route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.TRAIN, true);
        assertEquals(new Dollars("0"), route.mileageAllowance());
    }

    @Test
    public void mileageAllowanceMustBeRequested() {
        Route route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.PERSONAL_AUTO, false);
        assertEquals(new Dollars("0"), route.mileageAllowance());
    }

    @Test
    public void multiModeOfTransporationTrip() {
        Leg outbound1 = new Leg(new Address(), new Address(), 50, ModeOfTransportation.PERSONAL_AUTO);
        Leg outbound2 = new Leg(new Address(), new Address(), 10, ModeOfTransportation.CARPOOL);
        Leg return1 = new Leg(new Address(), new Address(), 10, ModeOfTransportation.CARPOOL);
        Leg return2 = new Leg(new Address(), new Address(), 50, ModeOfTransportation.PERSONAL_AUTO);
        Route route = new Route(ImmutableList.of(outbound1, outbound2),
                ImmutableList.of(return1, return2), MILEAGE_RATE, true);

        assertEquals(new Dollars("50.00"), route.mileageAllowance());
    }
}
