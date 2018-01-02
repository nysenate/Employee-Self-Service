package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaResponse;
import gov.nysenate.ess.travel.allowance.gsa.service.GsaResponseParser;
import gov.nysenate.ess.travel.fixtures.GsaApiResponseFixture;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Month;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class GsaResponseParserTest {

    private static GsaResponseParser parser;

    @BeforeClass
    public static void beforeClass() {
        parser = new GsaResponseParser(OutputUtils.jsonMapper);
    }

    @Test
    public void correctlyParsesGsaResponse() throws IOException {
        GsaResponse res = parser.parseGsaResponse(GsaApiResponseFixture.GsaApiResponseFY2018Zip10036());

        assertEquals(2018, res.getFiscalYear());
        assertEquals("10036", res.getZipcode());
        assertEquals("74", res.getMealTier());

        assertEquals(new BigDecimal("164"), res.getLodging(Month.JANUARY));
        assertEquals(new BigDecimal("164"), res.getLodging(Month.FEBRUARY));
        assertEquals(new BigDecimal("253"), res.getLodging(Month.MARCH));
        assertEquals(new BigDecimal("253"), res.getLodging(Month.APRIL));
        assertEquals(new BigDecimal("253"), res.getLodging(Month.MAY));
        assertEquals(new BigDecimal("253"), res.getLodging(Month.JUNE));
        assertEquals(new BigDecimal("230"), res.getLodging(Month.JULY));
        assertEquals(new BigDecimal("230"), res.getLodging(Month.AUGUST));
        assertEquals(new BigDecimal("291"), res.getLodging(Month.SEPTEMBER));
        assertEquals(new BigDecimal("291"), res.getLodging(Month.OCTOBER));
        assertEquals(new BigDecimal("291"), res.getLodging(Month.NOVEMBER));
        assertEquals(new BigDecimal("291"), res.getLodging(Month.DECEMBER));
    }

}
