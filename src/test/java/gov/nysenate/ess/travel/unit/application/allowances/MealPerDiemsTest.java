package gov.nysenate.ess.travel.unit.application.allowances;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.fixtures.TravelAddressFixture;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class MealPerDiemsTest {

    private static TravelAddress CAPITOL;
    private static TravelAddress AGENCY;
    private static final LocalDate TODAY = LocalDate.of(2019, 1, 15);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    @BeforeClass
    public static void setup() {
        CAPITOL = TravelAddressFixture.capital();
        AGENCY = TravelAddressFixture.agencyBuilding();
    }

    @Test
    public void duplicateDaysRemoved() {
        MealPerDiem capitolMealPerDiem = new MealPerDiem(CAPITOL, TODAY, new Dollars("100"), createMieWithTotal("100"));
        MealPerDiem agencyMealPerDiem = new MealPerDiem(AGENCY, TODAY, new Dollars("100"), createMieWithTotal("100"));
        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(capitolMealPerDiem, agencyMealPerDiem));
        assertTrue(mpds.allMealPerDiems().size() == 1);
    }

    @Test
    public void onlyHighestRateForDayKept() {
        MealPerDiem a = new MealPerDiem(CAPITOL, TODAY, new Dollars("23"), createMieWithTotal("23"));
        MealPerDiem b = new MealPerDiem(CAPITOL, TODAY, new Dollars("10"), createMieWithTotal("10"));
        MealPerDiem c = new MealPerDiem(CAPITOL, TODAY, new Dollars("20"), createMieWithTotal("20"));
        MealPerDiem d = new MealPerDiem(CAPITOL, TODAY, new Dollars("14"), createMieWithTotal("14"));
        MealPerDiem e = new MealPerDiem(CAPITOL, TODAY, new Dollars("28"), createMieWithTotal("28"));
        MealPerDiem f = new MealPerDiem(CAPITOL, TODAY, new Dollars("894"), createMieWithTotal("894"));
        MealPerDiem g = new MealPerDiem(CAPITOL, TODAY, new Dollars("12"), createMieWithTotal("12"));
        MealPerDiem h = new MealPerDiem(CAPITOL, TODAY, new Dollars("666"), createMieWithTotal("666"));

        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(a, b, c, d, e, f, g, h));
        assertEquals(f, mpds.allMealPerDiems().first());
    }

    @Test
    public void orderedByDate() {
        MealPerDiem capitolMealPerDiem = new MealPerDiem(CAPITOL, TOMORROW, new Dollars("100"), createMieWithTotal("100"));
        MealPerDiem agencyMealPerDiem = new MealPerDiem(AGENCY, TODAY, new Dollars("50"), createMieWithTotal("50"));
        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(capitolMealPerDiem, agencyMealPerDiem));

        ImmutableList<MealPerDiem> expected = ImmutableList.of(agencyMealPerDiem, capitolMealPerDiem);
        ImmutableList<MealPerDiem> actual = mpds.allMealPerDiems().asList();

        assertEquals(expected, actual);
    }


    private SenateMie createMieWithTotal(String total) {
        return new SenateMie(
                0,
                2019,
                new Dollars(total),
                new Dollars(total).multiply(new Dollars(".33")),
                new Dollars(total).multiply(new Dollars(".66"))
                );
    }
}
