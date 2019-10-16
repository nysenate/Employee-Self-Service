package gov.nysenate.ess.travel.unit.application.allowances;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
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
        MealPerDiem capitolMealPerDiem = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("100")));
        MealPerDiem agencyMealPerDiem = new MealPerDiem(AGENCY, new PerDiem(TODAY, new BigDecimal("100")));
        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(capitolMealPerDiem, agencyMealPerDiem));
        assertTrue(mpds.allMealPerDiems().size() == 1);
    }

    @Test
    public void onlyHighestRateForDayKept() {
        MealPerDiem a = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("23")));
        MealPerDiem b = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("10")));
        MealPerDiem c = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("20")));
        MealPerDiem d = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("14")));
        MealPerDiem e = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("28")));
        MealPerDiem f = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("894")));
        MealPerDiem g = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("12")));
        MealPerDiem h = new MealPerDiem(CAPITOL, new PerDiem(TODAY, new BigDecimal("666")));

        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(a, b, c, d, e, f, g, h));
        assertEquals(f, mpds.allMealPerDiems().first());
    }

    @Test
    public void orderedByDate() {
        MealPerDiem capitolMealPerDiem = new MealPerDiem(CAPITOL, new PerDiem(TOMORROW, new BigDecimal("100")));
        MealPerDiem agencyMealPerDiem = new MealPerDiem(AGENCY, new PerDiem(TODAY, new BigDecimal("50")));
        MealPerDiems mpds = new MealPerDiems(Sets.newHashSet(capitolMealPerDiem, agencyMealPerDiem));

        ImmutableList<MealPerDiem> expected = ImmutableList.of(agencyMealPerDiem, capitolMealPerDiem);
        ImmutableList<MealPerDiem> actual = mpds.allMealPerDiems().asList();

        assertEquals(expected, actual);
    }

}
