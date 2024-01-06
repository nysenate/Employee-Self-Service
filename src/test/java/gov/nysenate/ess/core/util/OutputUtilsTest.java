package gov.nysenate.ess.core.util;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class OutputUtilsTest
{
    @Test(expected = IllegalAccessException.class)
    public void testNoConstructor() throws Exception {
        Class.forName("gov.nysenate.ess.core.util.OutputUtils").getDeclaredConstructor().newInstance();
    }

    @Test
    public void testToJson_ISODates() {
        assertEquals("\"2015-01-01\"", OutputUtils.toJson(LocalDate.of(2015, 1, 1)));
        assertEquals("\"2015-01-01T12:12:12\"", OutputUtils.toJson(LocalDateTime.of(2015, 1, 1, 12, 12, 12)));
    }

    @Test
    public void testToJson_Nulls() {
        assertEquals("null", OutputUtils.toJson(null));
    }
}