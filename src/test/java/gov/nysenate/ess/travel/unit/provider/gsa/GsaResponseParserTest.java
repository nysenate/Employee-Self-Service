package gov.nysenate.ess.travel.unit.provider.gsa;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.fixtures.GsaApiResponseFixture;
import gov.nysenate.ess.travel.provider.gsa.GsaResponse;
import gov.nysenate.ess.travel.provider.gsa.GsaResponseId;
import gov.nysenate.ess.travel.provider.gsa.GsaResponseParser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class GsaResponseParserTest {

    private static GsaResponseParser parser;

    @BeforeClass
    public static void beforeClass() {
        parser = new GsaResponseParser(OutputUtils.jsonMapper);
    }

    @Test
    public void correctlyParsesGsaResponse() throws IOException {
        GsaResponse res = parser.parseGsaResponse(GsaApiResponseFixture.fy2018_zip10008_response());
        GsaResponseId resId = res.getId();

        assertEquals(2018, resId.getFiscalYear());
        assertEquals("10008", resId.getZipcode());
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

    @Test
    public void correctlyParsesHigherCountyRates() throws IOException {
        GsaResponse res = parser.parseGsaResponse(GsaApiResponseFixture.fy2018_zip10940_response());
        GsaResponseId resId = res.getId();

        assertEquals(2018, resId.getFiscalYear());
        assertEquals("10940", resId.getZipcode());
        assertEquals("59", res.getMealTier());
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 1, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 2, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 3, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 4, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 5, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 6, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 7, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 8, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 9, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 10, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 11, 1)));
        assertEquals(new BigDecimal("108"), res.getLodging(LocalDate.of(2018, 12, 1)));
    }

    @Test
    public void canDetectEmptyResponse() throws IOException {
        assertTrue(parser.isResponseEmpty(GsaApiResponseFixture.fy2999_zip11111_response()));
    }
}
