package gov.nysenate.ess.travel.unit.provider.miles;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.fixtures.MileageRateWebsiteFixture;
import gov.nysenate.ess.travel.provider.miles.MileageRate;
import gov.nysenate.ess.travel.provider.miles.MileageRateParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class MileageRateParserTest {

    private MileageRateParser mrp;

    @Before
    public void setup() {
        mrp = new MileageRateParser();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullContent_throwsIllegalArgumentException() {
        mrp.parseMileageRate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyContent_throwsIllegalArgumentException() {
        mrp.parseMileageRate("");
    }

    @Test
    public void parses2019Website() throws IOException {
        String content = MileageRateWebsiteFixture.html();
        MileageRate rate = mrp.parseMileageRate(content);

        assertEquals(rate.getStartDate(), LocalDate.of(2019, 1, 1));
        assertEquals(rate.getEndDate(), DateUtils.THE_FUTURE);
        assertEquals(rate.getRate(), new BigDecimal("0.58"));
    }
}
