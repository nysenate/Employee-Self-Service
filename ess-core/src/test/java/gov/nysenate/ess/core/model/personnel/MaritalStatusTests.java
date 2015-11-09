package gov.nysenate.ess.core.model.personnel;

import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Category(ProperTest.class)
public class MaritalStatusTests
{
    @Test
    public void testValueOfCode() throws Exception {
        assertEquals(MaritalStatus.SINGLE, MaritalStatus.valueOfCode("S"));
        assertEquals(MaritalStatus.MARRIED, MaritalStatus.valueOfCode("M"));
        assertEquals(MaritalStatus.DIVORCED, MaritalStatus.valueOfCode("D"));
        assertEquals(MaritalStatus.WIDOWED, MaritalStatus.valueOfCode("W"));
        // check lower cases too
        assertEquals(MaritalStatus.SINGLE, MaritalStatus.valueOfCode("s"));
        assertEquals(MaritalStatus.MARRIED, MaritalStatus.valueOfCode("m"));
        assertEquals(MaritalStatus.DIVORCED, MaritalStatus.valueOfCode("d"));
        assertEquals(MaritalStatus.WIDOWED, MaritalStatus.valueOfCode("w"));
        // and nulls
        assertNull(MaritalStatus.valueOfCode(""));
        assertNull(MaritalStatus.valueOfCode(null));
    }
}