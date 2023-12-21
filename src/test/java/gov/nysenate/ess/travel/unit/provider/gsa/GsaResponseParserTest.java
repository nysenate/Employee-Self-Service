package gov.nysenate.ess.travel.unit.provider.gsa;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.fixtures.GsaApiResponseFixture;
import gov.nysenate.ess.travel.provider.gsa.GsaResponse;
import gov.nysenate.ess.travel.provider.gsa.GsaResponseId;
import gov.nysenate.ess.travel.provider.gsa.GsaResponseParser;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class GsaResponseParserTest {
    private static final GsaResponseParser parser = new GsaResponseParser(OutputUtils.jsonMapper);

    @Test
    public void correctlyParsesGsaResponse() throws IOException {
        GsaResponse res = parser.parseGsaResponse(GsaApiResponseFixture.fy2018_zip10008_response());
        GsaResponseId resId = res.getId();

        assertEquals(2018, resId.getFiscalYear());
        assertEquals("10008", resId.getZipcode());
        assertEquals("74", res.getMealTier());
        LocalDate date = LocalDate.of(2018, 1, 1);
        List<Integer> expectedRates = List.of(0, 164, 164, 253, 253, 253, 253, 230, 230, 291, 291, 291, 291);
        for (int month = 1; month <= 12; month++) {
            assertEquals(expectedRates.get(month).intValue(), res.getLodging(date.withMonth(month)).intValue());
        }
    }

    @Test
    public void correctlyParsesHigherCountyRates() throws IOException {
        GsaResponse res = parser.parseGsaResponse(GsaApiResponseFixture.fy2018_zip10940_response());
        GsaResponseId resId = res.getId();

        assertEquals(2018, resId.getFiscalYear());
        assertEquals("10940", resId.getZipcode());
        assertEquals("59", res.getMealTier());
        LocalDate date = LocalDate.of(2018, 1, 1);
        for (int month = 1; month <= 12; month++) {
            assertEquals(108, res.getLodging(date.withMonth(month)).intValue());
        }
    }

    @Test
    public void canDetectEmptyResponse() throws IOException {
        assertTrue(parser.isResponseEmpty(GsaApiResponseFixture.fy2999_zip11111_response()));
        assertFalse(parser.isResponseEmpty(GsaApiResponseFixture.fy2018_zip10008_response()));
    }
}
