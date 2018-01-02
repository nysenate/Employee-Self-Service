package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.gsa.model.MealTier;
import gov.nysenate.ess.travel.allowance.gsa.service.MealTiersParser;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class MealTiersParserTest {

    private static MealTiersParser parser;
    /** Raw HTML from http://www.gsa.gov/mie as of 2018-01-01. */
    private static String html;

    @BeforeClass
    public static void before() throws IOException {
        parser = new MealTiersParser();
        html = IOUtils.toString(MealTiersParser.class.getResourceAsStream("/test_files/travel/GsaMealWebsiteHtml.txt"), "UTF-8");
    }

    @Test
    public void parsesMealRatesFromHtml() {
        Map<String, MealTier> expectedTiers = new HashMap<>();
        expectedTiers.put("51", new MealTier("51", "11", "12", "23", "5"));
        expectedTiers.put("54", new MealTier("54", "12", "13", "24", "5"));
        expectedTiers.put("59", new MealTier("59", "13", "15", "26", "5"));
        expectedTiers.put("64", new MealTier("64", "15", "16", "28", "5"));
        expectedTiers.put("69", new MealTier("69", "16", "17", "31", "5"));
        expectedTiers.put("74", new MealTier("74", "17", "18", "34", "5"));

        assertEquals(expectedTiers, parser.parseMealTiers(html));
    }

}
