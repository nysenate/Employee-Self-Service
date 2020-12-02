package gov.nysenate.ess.travel.unit.approval.reviewer;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.review.strategy.SenatorReviewerStrategy;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SenatorReviewerStrategyTest {

    private SenatorReviewerStrategy strategy;

    @Before
    public void setup() {
        strategy = new SenatorReviewerStrategy();
    }

    @Test
    public void givenNullLastReviewer_returnDea() {
        TravelRole actual = strategy.after(null);
        TravelRole expected = TravelRole.TRAVEL_ADMIN;
        assertEquals(expected, actual);
    }

    @Test
    public void givenDeaLastReviewer_returnSos() {
        TravelRole actual = strategy.after(TravelRole.TRAVEL_ADMIN);
        TravelRole expected = TravelRole.SECRETARY_OF_THE_SENATE;
        assertEquals(expected, actual);
    }

    @Test
    public void givenSosLastReviewer_returnMaj() {
        TravelRole actual = strategy.after(TravelRole.SECRETARY_OF_THE_SENATE);
        TravelRole expected = TravelRole.MAJORITY_LEADER;
        assertEquals(expected, actual);
    }

    @Test
    public void givenMajLastReviewer_returnNone() {
        TravelRole actual = strategy.after(TravelRole.MAJORITY_LEADER);
        TravelRole expected = TravelRole.NONE;
        assertEquals(expected, actual);
    }

    @Test
    public void givenNoneLastReviewer_returnNone() {
        TravelRole actual = strategy.after(TravelRole.NONE);
        TravelRole expected = TravelRole.NONE;
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenSupLastReviewer_throwException() {
        strategy.after(TravelRole.SUPERVISOR);
    }

}
