package gov.nysenate.ess.core.model.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class LocationIdTest {

    @Test
    public void giveValidLocationId_isSyntacticallyValid() {
        LocationId locId = new LocationId("A42FB", LocationType.STORAGE);
        assertTrue(locId.isSyntacticallyValid());
    }

    @Test
    public void givenNullValues_notSyntacticallyValid() {
        LocationId locId = new LocationId(null, LocationType.STORAGE);
        assertFalse(locId.isSyntacticallyValid());

        locId = new LocationId(null, null);
        assertFalse(locId.isSyntacticallyValid());

        locId = new LocationId("A42FB", null);
        assertFalse(locId.isSyntacticallyValid());

        locId = new LocationId(null, 'W');
        assertFalse(locId.isSyntacticallyValid());

        locId = new LocationId(null);
        assertFalse(locId.isSyntacticallyValid());
    }

    @Test
    public void handlesStringsMissingDash() {
        LocationId locId = new LocationId("A42FBW");
        assertFalse(locId.isSyntacticallyValid());
    }

    @Test
    public void emptyCodeIsInvalid() {
        LocationId locId = new LocationId("", 'W');
        assertFalse(locId.isSyntacticallyValid());
    }
}
