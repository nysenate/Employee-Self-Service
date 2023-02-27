package gov.nysenate.ess.travel.unit.application.allowances;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.route.Leg;
import gov.nysenate.ess.travel.request.route.ModeOfTransportation;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.fixtures.TravelAddressFixture;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class MileagePerDiemsTest {

    private Destination from = new Destination(TravelAddressFixture.albany(), LocalDate.now(), LocalDate.now());
    private Destination to = new Destination(TravelAddressFixture.cliftonPark(), LocalDate.now(), LocalDate.now());
    private PerDiem perDiem = new PerDiem(LocalDate.now(), new Dollars("0.50"));

    @Test(expected = NullPointerException.class)
    public void givenNull_thenThrowNullPointerException() {
        MileagePerDiems mpd = new MileagePerDiems(null);
    }

    @Test
    public void givenEmpty_thenPerDiemIsZero() {
        MileagePerDiems mpd = new MileagePerDiems(new ArrayList<>());
        assertEquals(Dollars.ZERO, mpd.totalPerDiem());
    }

    @Test
    public void givenLongTrip_thenCorrectlyCalculatesReimbursement() {
        Leg outboundLeg = new Leg(0, from, to, ModeOfTransportation.PERSONAL_AUTO, 100, perDiem, true, true);
        Leg returnLeg = new Leg(0, to, from, ModeOfTransportation.PERSONAL_AUTO, 100, perDiem, false, true);
        MileagePerDiems mpd = new MileagePerDiems(Arrays.asList(outboundLeg, returnLeg));
        assertEquals(new Dollars("100"), mpd.totalPerDiem());
    }

    /**
     * The outbound mileage must be greater than the MILE_THRESHOLD to receive mileage reimbursement.
     */
    @Test
    public void toQualityForMileageReimbursement_outboundLegMustBeGreaterThanMileageThreshold() {
        Leg outboundLeg = new Leg(0, from, to, ModeOfTransportation.TRAIN, 100, perDiem, true, true);
        Leg returnLeg = new Leg(0, to, from, ModeOfTransportation.PERSONAL_AUTO, 100, perDiem, false, true);

        MileagePerDiems mpd = new MileagePerDiems(Arrays.asList(outboundLeg, returnLeg));
        assertTrue(mpd.tripQualifiesForReimbursement());
    }

    @Test
    public void givenOutboundLeg_thenTripQualifiesForReimbursement() {
        boolean isOutbound = true;
        Leg leg = new Leg(0, from, to, ModeOfTransportation.PERSONAL_AUTO, 99999.0, perDiem, isOutbound, true);

        MileagePerDiems mpd = new MileagePerDiems(Arrays.asList(leg));
        assertTrue(mpd.tripQualifiesForReimbursement());
    }

    @Test
    public void givenModeOfTransportation_onlyPersonalAutoQualifiesForReimbursement() {
        assertFalse(modeOfTransportationQualifiesForReimbursement(ModeOfTransportation.CARPOOL));
        assertFalse(modeOfTransportationQualifiesForReimbursement(ModeOfTransportation.AIRPLANE));
        assertFalse(modeOfTransportationQualifiesForReimbursement(ModeOfTransportation.SENATE_VEHICLE));
        assertFalse(modeOfTransportationQualifiesForReimbursement(ModeOfTransportation.TRAIN));

        assertTrue(modeOfTransportationQualifiesForReimbursement(ModeOfTransportation.PERSONAL_AUTO));
    }

    private boolean modeOfTransportationQualifiesForReimbursement(ModeOfTransportation mot) {
        Leg leg = new Leg(0, from, to, mot, 100, perDiem, true, true);
        MileagePerDiems mpd = new MileagePerDiems(Arrays.asList(leg));
        return mpd.tripQualifiesForReimbursement();
    }
}
