package gov.nysenate.ess.core.model.auth;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class DateTimeRangePermissionTest {

    private static final String permissionStrA = "a";
    private static final String permissionStrA1 = "a:1";

    private static final LocalDate d1 = LocalDate.of(2015, 1, 1);
    private static final LocalDate d2 = LocalDate.of(2015, 2, 1);
    private static final LocalDate d3 = LocalDate.of(2015, 3, 1);
    private static final LocalDate d4 = LocalDate.of(2015, 4, 1);

    @Before
    public void preconditions() {
        WildcardPermission pA = new WildcardPermission(permissionStrA);
        WildcardPermission pA1 = new WildcardPermission(permissionStrA1);
        assertTrue(pA.implies(pA));
        assertTrue(pA.implies(pA1));
        assertFalse(pA1.implies(pA));
        assertTrue(pA1.implies(pA1));
        assertTrue(d1.isBefore(d2));
        assertTrue(d2.isBefore(d3));
        assertTrue(d3.isBefore(d4));
    }

    @Test
    public void encloseImpliesTest() {
        DateTimeRangePermission permission = new DateTimeRangePermission(permissionStrA,
                Range.closedOpen(d1.atStartOfDay(), d3.atStartOfDay()));
        DateTimeRangePermission enclosed = new DateTimeRangePermission(permissionStrA1,
                Range.closedOpen(d2.atStartOfDay(), d3.atStartOfDay()));
        assertTrue(permission.implies(enclosed));
    }

    @Test
    public void encloseNotImpliesTest() {
        DateTimeRangePermission permission = new DateTimeRangePermission(permissionStrA,
                Range.closedOpen(d1.atStartOfDay(), d3.atStartOfDay()));
        DateTimeRangePermission notEnclosed = new DateTimeRangePermission(permissionStrA1,
                Range.closedOpen(d2.atStartOfDay(), d4.atStartOfDay()));
        assertFalse(permission.implies(notEnclosed));
    }

    @Test
    public void encloseStringMismatchTest() {
        DateTimeRangePermission permission = new DateTimeRangePermission(permissionStrA1,
                Range.closedOpen(d1.atStartOfDay(), d3.atStartOfDay()));
        DateTimeRangePermission enclosedDateNotStr = new DateTimeRangePermission(permissionStrA,
                Range.closedOpen(d2.atStartOfDay(), d3.atStartOfDay()));
        assertFalse(permission.implies(enclosedDateNotStr));
    }

    @Test
    public void encloseImpliesSelfTest() {
        DateTimeRangePermission permission = new DateTimeRangePermission(permissionStrA,
                Range.closedOpen(d1.atStartOfDay(), d3.atStartOfDay()));
        assertTrue(permission.implies(permission));
    }

    @Test
    public void intersectImpliesTest() {
        DateTimeRangePermission permission = new DateTimeRangePermission(permissionStrA,
                Range.closedOpen(d1.atStartOfDay(), d3.atStartOfDay()));
        DateTimeRangePermission enclosed = new DateTimeRangePermission(permissionStrA1,
                Range.closedOpen(d2.atStartOfDay(), d4.atStartOfDay()), false);
        assertTrue(permission.implies(enclosed));
    }

    @Test
    public void intersectNotImpliesTest() {
        DateTimeRangePermission permission = new DateTimeRangePermission(permissionStrA,
                Range.closedOpen(d1.atStartOfDay(), d3.atStartOfDay()));
        DateTimeRangePermission enclosed = new DateTimeRangePermission(permissionStrA1,
                Range.closedOpen(d3.atStartOfDay(), d4.atStartOfDay()), false);
        assertFalse(permission.implies(enclosed));
    }
}