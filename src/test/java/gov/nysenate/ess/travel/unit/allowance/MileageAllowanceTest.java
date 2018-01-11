package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.ReimbursableLeg;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class MileageAllowanceTest {

    private static final Leg stubLeg = new Leg(new Address(), new Address());

    @Test(expected = NullPointerException.class)
    public void rateCannotBeNull() {
        new MileageAllowance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rateCannotBeNegative() {
        new MileageAllowance(new BigDecimal("-0.10"));
    }

    @Test
    public void emptyTrip_ZeroReimbursement() {
        MileageAllowance allowance = new MileageAllowance(new BigDecimal("0.50"));
        assertEquals(new BigDecimal("0.00"), allowance.getReimbursement());
    }

    @Test
    public void outboundLessThan35Miles_ZeroReimbursement() {
        MileageAllowance allowance = new MileageAllowance(new BigDecimal("0.50"));
        allowance = allowance.addOutboundLeg(new ReimbursableLeg(stubLeg, new BigDecimal("34.9")));
        allowance = allowance.addReturnLeg(new ReimbursableLeg(stubLeg, new BigDecimal("34.9")));
        assertEquals(new BigDecimal("0.00"), allowance.getReimbursement());
    }

    @Test
    public void outboundOver35Miles_EntireTripReimbursable() {
        MileageAllowance allowance = new MileageAllowance(new BigDecimal("0.50"));
        allowance = allowance.addOutboundLeg(new ReimbursableLeg(stubLeg, new BigDecimal("35.0")));
        allowance = allowance.addReturnLeg(new ReimbursableLeg(stubLeg, new BigDecimal("35.0")));
        assertEquals(new BigDecimal("35.00"), allowance.getReimbursement());
    }

    @Test
    public void testReimbursementOnMultiLegTrip() {
        MileageAllowance allowance = new MileageAllowance(new BigDecimal("0.50"));
        allowance = allowance.addOutboundLeg(new ReimbursableLeg(stubLeg, new BigDecimal("10.5")));
        allowance = allowance.addOutboundLeg(new ReimbursableLeg(stubLeg, new BigDecimal("33.3")));
        allowance = allowance.addOutboundLeg(new ReimbursableLeg(stubLeg, new BigDecimal("2.7")));

        allowance = allowance.addReturnLeg(new ReimbursableLeg(stubLeg, new BigDecimal("13.1")));
        assertEquals(new BigDecimal("29.80"), allowance.getReimbursement());
    }
}
