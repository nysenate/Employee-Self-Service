package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.gsa.model.LodgingAllowance;
import gov.nysenate.ess.travel.allowance.gsa.model.MealAllowance;
import gov.nysenate.ess.travel.allowance.mileage.model.MileageAllowance;
import gov.nysenate.ess.travel.application.model.TravelAllowances;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(UnitTest.class)
public class TravelAllowancesTest {

    private MealAllowance meal =  new MealAllowance();
    private LodgingAllowance lodging = new LodgingAllowance();
    private MileageAllowance mileage = new MileageAllowance(new BigDecimal("0"));

    @Test
    public void nullValues_areInvalid() {
        try {
            new TravelAllowances(null, lodging, mileage, "0", "0", "0", "0");
            fail("Meal Allowance cannot be null");
        } catch (NullPointerException ex) {}

         try {
            new TravelAllowances(meal, null, mileage, "0", "0", "0", "0");
            fail("Lodging Allowance cannot be null");
        } catch (NullPointerException ex) {}

         try {
            new TravelAllowances(meal, lodging, null, "0", "0", "0", "0");
            fail("MileageAllowance cannot be null");
        } catch (NullPointerException ex) {}
    }

    @Test (expected = NumberFormatException.class)
    public void emptyStrings_areInvalid() {
        new TravelAllowances(meal, lodging, mileage, "", "", "", "");
    }

    @Test (expected = NumberFormatException.class)
    public void nonNumberStrings_areInvalid() {
        new TravelAllowances(meal, lodging, mileage, "445z", "x1", "_", "}");
    }

    @Test (expected = IllegalArgumentException.class)
    public void negativeValues_areInvalid() {
        new TravelAllowances(meal, lodging, mileage, "-12.00", "-1", "-2.2", "-8");
    }

    @Test
    public void roundToTwoDigits() {
        TravelAllowances actual = new TravelAllowances(meal, lodging, mileage, "0.005", "0.0000001", "0", "3.14159265358");
        TravelAllowances expected = new TravelAllowances(meal, lodging, mileage, "0.01", "0.00", "0", "3.14");
        assertEquals(expected, actual);
    }

    // TODO test total with values in meal, lodging, and mileage allowances
    @Test
    public void totalSumsAllAllowances() {
        TravelAllowances allowances = new TravelAllowances(meal, lodging, mileage, "3.50", "7.25", "0.51", "220");
        assertEquals("231.26", allowances.total().toString());
    }
}
