package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.gsa.model.MealRates;
import gov.nysenate.ess.travel.allowance.gsa.service.MealRatesParser;
import gov.nysenate.ess.travel.fixtures.MealRatesFixture;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class MealRatesParserTest {

    private static MealRatesParser parser;
    /** Raw HTML from http://www.gsa.gov/mie as of 2018-01-01. */
    private static String html;

    @BeforeClass
    public static void before() throws IOException {
        parser = new MealRatesParser();
        html = IOUtils.toString(MealRatesParser.class.getResourceAsStream("/test_files/travel/GsaMealWebsiteHtml.txt"), "UTF-8");
    }

    @Test
    public void parsesMealRatesFromHtml() {
        MealRates expectedRates = MealRatesFixture.mealRatesFor2018();
        assertEquals(expectedRates, parser.parseMealRates(html));
    }

}
