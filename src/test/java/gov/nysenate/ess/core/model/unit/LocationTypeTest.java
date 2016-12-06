package gov.nysenate.ess.core.model.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LocationTypeTest
{
    /** Test the value of code method, should be case insensitive. */
    @Test
    public void testValueOfCode() throws Exception {
        assertEquals(LocationType.WAREHOUSE, LocationType.valueOfCode('H'));
        assertEquals(LocationType.WAREHOUSE, LocationType.valueOfCode('h'));
        assertEquals(LocationType.STORAGE, LocationType.valueOfCode('S'));
        assertEquals(LocationType.STORAGE, LocationType.valueOfCode('s'));
        assertEquals(LocationType.SUPPLY, LocationType.valueOfCode('P'));
        assertEquals(LocationType.SUPPLY, LocationType.valueOfCode('p'));
        assertEquals(LocationType.WORK, LocationType.valueOfCode('W'));
        assertEquals(LocationType.WORK, LocationType.valueOfCode('w'));
    }
}