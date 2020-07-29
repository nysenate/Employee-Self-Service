package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.Amendment;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.Version;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelApplicationTest {

    @Test
    public void givenNoAmendmentsApproved_activeAmendmentIsMostRecent() {
        Amendment a = createAmendment(Version.A);
        Amendment b = createAmendment(Version.B);
        TravelApplication app = new TravelApplication(1, new Employee(), Lists.newArrayList(a, b));

        assertEquals(b, app.activeAmendment());
    }

    @Ignore
    @Test
    public void givenApprovedAmendment_activeAmendmentIsMostRecentlyApproved() {
        Amendment a = createAmendment(Version.A);
        a.approve();
        Amendment b = createAmendment(Version.B);
        TravelApplication app = new TravelApplication(1, new Employee(), Lists.newArrayList(a, b));

        assertEquals(a, app.activeAmendment());

        Amendment c = createAmendment(Version.C);
        c.approve();
//        app.amend(c);

        assertEquals(c, app.activeAmendment());
    }

    private Amendment createAmendment(Version v) {
        return new Amendment.Builder()
                .withVersion(v)
                .build();
    }
}
