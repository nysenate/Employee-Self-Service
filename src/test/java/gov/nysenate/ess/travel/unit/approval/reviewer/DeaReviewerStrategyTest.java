package gov.nysenate.ess.travel.unit.approval.reviewer;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.review.strategy.DeaReviewerStrategy;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class DeaReviewerStrategyTest {

    private DeaReviewerStrategy strategy;

    @Before
    public void setup() {
        strategy = new DeaReviewerStrategy();
    }

    @Test
    public void givenNullLastReviewer_returnSos() {
        TravelRole actual = strategy.after(null);
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

    @Test (expected = IllegalArgumentException.class)
    public void givenSupLastReviewer_throwException() {
        strategy.after(TravelRole.SUPERVISOR);
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenDeaLastReviewer_throwException() {
        strategy.after(TravelRole.DEPUTY_EXECUTIVE_ASSISTANT);
    }
}
