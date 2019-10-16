package gov.nysenate.ess.core.model.unit;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.*;

@Category(UnitTest.class)
public class AddressTest
{
    public static List<Address> addressList;
    static {
        addressList = Lists.newArrayList(
            new Address("71 14th Street", "", "Troy", "NY", "", ""),
            new Address("214 8th Street", "", "Troy", "NY", "12180", ""),
            new Address("101 East State Street", "", "Olean", "NY", "14760", ""),
            new Address("706 washington", "", "Olean", "NY", "14760", ""),
            new Address("2012 E Rivr Road", "", "", "", "", ""),
            new Address("44 Fairlawn Ave", "Apt 2B", "Albany", "NY", "12203", ""),
            new Address("", "", "", "", "18542", ""),
            new Address("", "", "", "", "", "  "));
    }

    /** An address is only parsed if it contains anything other than an addr1 line. */
    @Test
    public void testIsParsed() throws Exception {
        assertTrue(addressList.get(0).isParsed());
        assertTrue(addressList.get(6).isParsed());  // Only zip5
        assertFalse(addressList.get(4).isParsed()); // Only addr1
        assertFalse(addressList.get(7).isParsed()); // Nothing
        assertFalse(new Address().isParsed()); // Nothing
    }

    /** An address with no fields set is empty, regardless of whitespace. */
    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(addressList.get(7).isEmpty());
        assertTrue(new Address(" ").isEmpty());
        assertFalse(addressList.get(6).isEmpty());
    }

    /** If the address is parse, the toString should display the address in a standard format. */
    @Test
    public void testToString() throws Exception {
        assertEquals("71 14th Street, Troy, NY", addressList.get(0).toString());
        assertEquals("101 East State Street, Olean, NY 14760", addressList.get(2).toString());
        assertEquals("18542", addressList.get(6).toString());
    }

    /** Normalized string will remove the dashes in the building number. */
    @Test
    public void toStringStripBuildingNumber() throws Exception {
        assertEquals("8510 143st, Sample City, NY 10000",
            new Address("85-10 143st", "", "Sample City", "NY", "10000", "").toStringStripBuildingNumber());
    }
}