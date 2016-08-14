package gov.nysenate.ess.core.util;

import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

@Category(ProperTest.class)
public class OutputUtilsTests
{
    @Test(expected = IllegalAccessException.class)
    public void testNoConstructor() throws Exception {
        Class.forName("gov.nysenate.ess.core.util.OutputUtils").newInstance();
    }

    @Test
    public void testToJson_ISODates() throws Exception {
        assertEquals("\"2015-01-01\"", OutputUtils.toJson(LocalDate.of(2015, 1, 1)));
        assertEquals("\"2015-01-01T12:12:12\"", OutputUtils.toJson(LocalDateTime.of(2015, 1, 1, 12, 12, 12)));
    }

    @Test
    public void testToJson_Nulls() throws Exception {
        assertEquals("null", OutputUtils.toJson(null));
    }
}