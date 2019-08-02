package gov.nysenate.ess.travel.unit.application;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.Amendment;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationStatus;
import gov.nysenate.ess.travel.application.Version;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.route.Route;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@UnitTest
public class TravelApplicationTest {

    @Test
    public void givenNoAmendmentsApproved_activeAmendmentIsMostRecent() {
        Amendment a = createAmendment(Version.A);
        Amendment b = createAmendment(Version.B);
        TravelApplication app = new TravelApplication(1, new Employee());
        app.amend(a);
        app.amend(b);

        assertEquals(b, app.activeAmendment());
    }

    @Test
    public void givenApprovedAmendment_activeAmendmentIsMostRecentlyApproved() {
        Amendment a = createAmendment(Version.A);
        a.approve();
        Amendment b = createAmendment(Version.B);
        TravelApplication app = new TravelApplication(1, new Employee());
        app.amend(a);
        app.amend(b);

        assertEquals(a, app.activeAmendment());

        Amendment c = createAmendment(Version.C);
        c.approve();
        app.amend(c);

        assertEquals(c, app.activeAmendment());
    }

    private Amendment createAmendment(Version v) {
        return new Amendment(0, 0, v, "", Route.EMPTY_ROUTE, new Allowances(),
                new PerDiemOverrides(), new TravelApplicationStatus(), new ArrayList<>(),
                LocalDateTime.now(), new Employee());
    }
}
