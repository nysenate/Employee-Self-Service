package gov.nysenate.ess.travel.unit.application;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.Amendment;
import gov.nysenate.ess.travel.application.TravelApplication;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class TravelApplicationTest {

    @Test
    public void givenNoAmendmentsApproved_activeAmendmentIsMostRecent() {
        Amendment a = new Amendment(1);
        Amendment b = new Amendment(2);
        TravelApplication app = new TravelApplication(1, new Employee(), a);
        app.addAmendment(b);

        assertEquals(b, app.activeAmendment());
    }

    @Test
    public void givenApprovedAmendment_activeAmendmentIsMostRecentlyApproved() {
        Amendment a = new Amendment(1);
        a.approve();
        Amendment b = new Amendment(2);
        TravelApplication app = new TravelApplication(1, new Employee(), a);
        app.addAmendment(b);

        assertEquals(a, app.activeAmendment());

        Amendment c = new Amendment(3);
        c.approve();
        app.addAmendment(c);

        assertEquals(c, app.activeAmendment());
    }
}
