package gov.nysenate.ess.travel.unit.provider.miles;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.provider.miles.MileageRate;
import gov.nysenate.ess.travel.provider.miles.MileageRateParser;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class MileageRateParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullContent_throwsIllegalArgumentException() {
        MileageRateParser.parseMileageRate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyContent_throwsIllegalArgumentException() {
        MileageRateParser.parseMileageRate("");
    }

    @Test
    public void parses2019Website() throws IOException {
        String path = MileageRateParserTest.class.getClassLoader().getResource("travel/GsaMileageRateWebsiteHtml.txt").getFile();
        String content = FileUtils.readFileToString(new File(path), Charset.defaultCharset());
        MileageRate rate = MileageRateParser.parseMileageRate(content);

        assertEquals(rate.startDate(), LocalDate.of(2019, 1, 1));
        assertEquals(rate.endDate(), DateUtils.THE_FUTURE);
        assertEquals(rate.rate(), new BigDecimal("0.58"));
    }
}
