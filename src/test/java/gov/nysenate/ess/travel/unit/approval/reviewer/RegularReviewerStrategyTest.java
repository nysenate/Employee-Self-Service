package gov.nysenate.ess.travel.unit.approval.reviewer;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.approval.reviewer.RegularReviewerStrategy;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class RegularReviewerStrategyTest {

    private RegularReviewerStrategy strategy;

    @Before
    public void setup() {
        strategy = new RegularReviewerStrategy();
    }

    @Test
    public void givenNullLastReviewer_returnSupervisor() {
        TravelRole actual = strategy.after(null);
        TravelRole expected = TravelRole.SUPERVISOR;
        assertEquals(expected, actual);
    }

    @Test
    public void givenSupervisorLastReviewer_returnDea() {
        TravelRole actual = strategy.after(TravelRole.SUPERVISOR);
        TravelRole expected = TravelRole.DEPUTY_EXECUTIVE_ASSISTANT;
        assertEquals(expected, actual);
    }

    @Test
    public void givenDeaLastReviewer_returnSos() {
        TravelRole actual = strategy.after(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT);
        TravelRole expected = TravelRole.SECRETARY_OF_THE_SENATE;
        assertEquals(expected, actual);
    }

    @Test
    public void givenSosLastReviewer_returnNone() {
        TravelRole actual = strategy.after(TravelRole.SECRETARY_OF_THE_SENATE);
        TravelRole expected = TravelRole.NONE;
        assertEquals(expected, actual);
    }

    @Test
    public void givenNoneLastReviewer_returnNone() {
        TravelRole actual = strategy.after(TravelRole.NONE);
        TravelRole expected = TravelRole.NONE;
        assertEquals(expected, actual);
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenMajLastReviewer_throwException() {
        strategy.after(TravelRole.MAJORITY_LEADER);
    }
}
