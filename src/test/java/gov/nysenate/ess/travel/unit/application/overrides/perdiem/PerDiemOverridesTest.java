package gov.nysenate.ess.travel.unit.application.overrides.perdiem;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@UnitTest
public class PerDiemOverridesTest {

    private PerDiemOverrides overrides;

    @Before
    public void before() {
        overrides = new PerDiemOverrides();
    }

    @Test
    public void overridesInitializedToZero() {
        assertEquals(Dollars.ZERO, overrides.mileageOverride());
        assertEquals(Dollars.ZERO, overrides.mealsOverride());
        assertEquals(Dollars.ZERO, overrides.lodgingOverride());
    }

    @Test
    public void overridesInitializedToNotOverridden() {
        assertFalse(overrides.isMileageOverridden());
        assertFalse(overrides.isMealsOverridden());
        assertFalse(overrides.isLodgingOverridden());
    }

    @Test
    public void settingOverridesAreSaved() {
        Dollars expectedMileage = new Dollars("30.78");
        overrides.setMileageOverride(expectedMileage);

        Dollars expectedMeals = new Dollars("15.00");
        overrides.setMealsOverride(expectedMeals);

        Dollars expectedLodging = new Dollars("45.00");
        overrides.setLodgingOverride(expectedLodging);

        assertEquals(expectedMileage, overrides.mileageOverride());
        assertEquals(expectedMeals, overrides.mealsOverride());
        assertEquals(expectedLodging, overrides.lodgingOverride());
    }

    @Test
    public void greaterThanZeroIsConsideredOverridden() {
        overrides.setMileageOverride(new Dollars("30"));
        overrides.setMealsOverride(new Dollars("0.01"));
        overrides.setLodgingOverride(new Dollars("329"));

        assertTrue(overrides.isMileageOverridden());
        assertTrue(overrides.isMealsOverridden());
        assertTrue(overrides.isLodgingOverridden());
    }

    @Test(expected = IllegalArgumentException.class)
    public void mileageOverrideCannotBeNegative() {
        overrides.setMileageOverride(new Dollars("-5"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void mealsOverrideCannotBeNegative() {
        overrides.setMealsOverride(new Dollars("-5233142"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void lodgingOverrideCannotBeNegative() {
        overrides.setLodgingOverride(new Dollars("-0.05"));
    }
}
