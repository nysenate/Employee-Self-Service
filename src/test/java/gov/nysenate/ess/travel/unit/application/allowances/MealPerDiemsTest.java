package gov.nysenate.ess.travel.unit.application.allowances;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMie;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class MealPerDiemsTest {

    private static GoogleAddress CAPITOL = new GoogleAddress(1, "", "", "");
    private static GoogleAddress AGENCY = new GoogleAddress(1, "", "", "");
    private static final LocalDate TODAY = LocalDate.of(2019, 1, 15);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    @BeforeClass
    public static void setup() {
        CAPITOL.setAddr1("100 State Street");
        CAPITOL.setCity("Albany");
        CAPITOL.setState("New York");
        CAPITOL.setZip5("12208");

        AGENCY.setAddr1("South Mall Arterial");
        AGENCY.setCity("Albany");
        AGENCY.setState("New York");
        AGENCY.setZip5("12210");
    }

    @Test
    public void duplicateDaysRemoved() {
        MealPerDiem capitolMealPerDiem = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("100"));
        MealPerDiem agencyMealPerDiem = new MealPerDiem(AGENCY, TODAY, createMieWithTotal("100"));
        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(capitolMealPerDiem, agencyMealPerDiem));
        assertTrue(mpds.allMealPerDiems().size() == 1);
    }

    @Test
    public void onlyHighestRateForDayKept() {
        MealPerDiem a = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("23"));
        MealPerDiem b = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("10"));
        MealPerDiem c = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("20"));
        MealPerDiem d = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("14"));
        MealPerDiem e = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("28"));
        MealPerDiem f = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("894"));
        MealPerDiem g = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("12"));
        MealPerDiem h = new MealPerDiem(CAPITOL, TODAY, createMieWithTotal("666"));

        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(a, b, c, d, e, f, g, h));
        assertEquals(f, mpds.allMealPerDiems().first());
    }

    @Test
    public void orderedByDate() {
        MealPerDiem capitolMealPerDiem = new MealPerDiem(CAPITOL, TOMORROW, createMieWithTotal("100"));
        MealPerDiem agencyMealPerDiem = new MealPerDiem(AGENCY, TODAY, createMieWithTotal("50"));
        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(capitolMealPerDiem, agencyMealPerDiem));

        ImmutableList<MealPerDiem> expected = ImmutableList.of(agencyMealPerDiem, capitolMealPerDiem);
        ImmutableList<MealPerDiem> actual = mpds.allMealPerDiems().asList();

        assertEquals(expected, actual);
    }


    private GsaMie createMieWithTotal(String total) {
        return new GsaMie(
                0,
                2019,
                new Dollars(total),
                new Dollars(total).divide(3),
                new Dollars(total).divide(3),
                new Dollars(total).divide(3),
                new Dollars("5"),
                new Dollars(total).divide(2)
                );
    }
}
