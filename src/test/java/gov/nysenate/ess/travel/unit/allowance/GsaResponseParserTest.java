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
import java.time.LocalDate;
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

        assertEquals(new BigDecimal("164"), res.getLodging(LocalDate.of(2018, 1, 1)));
        assertEquals(new BigDecimal("164"), res.getLodging(LocalDate.of(2018, 2, 1)));
        assertEquals(new BigDecimal("253"), res.getLodging(LocalDate.of(2018, 3, 1)));
        assertEquals(new BigDecimal("253"), res.getLodging(LocalDate.of(2018, 4, 1)));
        assertEquals(new BigDecimal("253"), res.getLodging(LocalDate.of(2018, 5, 1)));
        assertEquals(new BigDecimal("253"), res.getLodging(LocalDate.of(2018, 6, 1)));
        assertEquals(new BigDecimal("230"), res.getLodging(LocalDate.of(2018, 7, 1)));
        assertEquals(new BigDecimal("230"), res.getLodging(LocalDate.of(2018, 8, 1)));
        assertEquals(new BigDecimal("291"), res.getLodging(LocalDate.of(2018, 9, 1)));
        assertEquals(new BigDecimal("291"), res.getLodging(LocalDate.of(2018, 10, 1)));
        assertEquals(new BigDecimal("291"), res.getLodging(LocalDate.of(2018, 11, 1)));
        assertEquals(new BigDecimal("291"), res.getLodging(LocalDate.of(2018, 12, 1)));
    }

}
