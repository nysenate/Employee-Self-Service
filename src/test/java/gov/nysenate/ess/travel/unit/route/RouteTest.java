package gov.nysenate.ess.travel.unit.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.route.Leg;
import gov.nysenate.ess.travel.route.ModeOfTransportation;
import gov.nysenate.ess.travel.route.Route;
import gov.nysenate.ess.travel.fixtures.RouteFixture;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@UnitTest
public class RouteTest {

    @Test
    public void longTripGetsMileageAllowance() {
        Route route = RouteFixture.longOneDestinationRoute();
        assertEquals(new Dollars("218.00"), route.mileageAllowance());
    }

    @Test
    public void outboundTrimLessThan35MilesNoAllowance() {
        Route route = RouteFixture.shortOneDestinationRoute();
        assertEquals(new Dollars("0"), route.mileageAllowance());
    }

    @Test
    public void testMultiDestinationRoute() {
        Route route = RouteFixture.multiDestinationRoute();
        assertEquals(new Dollars("40.88"), route.mileageAllowance());
    }

    @Test
    public void mileageAllowanceRequiresPersonalAuto() {
        Route route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.SENATE_VEHICLE);
        assertEquals(new Dollars("0"), route.mileageAllowance());

        route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.OTHER);
        assertEquals(new Dollars("0"), route.mileageAllowance());

        route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.AIRPLANE);
        assertEquals(new Dollars("0"), route.mileageAllowance());

        route = RouteFixture.longOneDestinationRoute(ModeOfTransportation.TRAIN);
        assertEquals(new Dollars("0"), route.mileageAllowance());
    }

    @Test
    public void mileageAllowanceMustBeRequested() {
        Leg outbound1 = new Leg(new Address(), new Address(), 50, ModeOfTransportation.PERSONAL_AUTO, LocalDate.now(), false);
        Leg return1 = new Leg(new Address(), new Address(), 50, ModeOfTransportation.PERSONAL_AUTO, LocalDate.now(), false);
        Route route = new Route(ImmutableList.of(outbound1), ImmutableList.of(return1), RouteFixture.MILEAGE_RATE);
        assertEquals(new Dollars("0"), route.mileageAllowance());
    }

    @Test
    public void multiModeOfTransporationTrip() {
        Leg outbound1 = new Leg(new Address(), new Address(), 50, ModeOfTransportation.PERSONAL_AUTO, LocalDate.now(), true);
        Leg outbound2 = new Leg(new Address(), new Address(), 10, ModeOfTransportation.OTHER, LocalDate.now(), true);
        Leg return1 = new Leg(new Address(), new Address(), 10, ModeOfTransportation.OTHER, LocalDate.now(), true);
        Leg return2 = new Leg(new Address(), new Address(), 50, ModeOfTransportation.PERSONAL_AUTO, LocalDate.now(), true);
        Route route = new Route(ImmutableList.of(outbound1, outbound2),
                ImmutableList.of(return1, return2), RouteFixture.MILEAGE_RATE);

        assertEquals(new Dollars("54.50"), route.mileageAllowance());
    }
}
