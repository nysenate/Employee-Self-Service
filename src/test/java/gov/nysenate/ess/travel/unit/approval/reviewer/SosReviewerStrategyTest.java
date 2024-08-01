package gov.nysenate.ess.travel.unit.approval.reviewer;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.review.strategy.SosReviewerStrategy;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class SosReviewerStrategyTest {

    private SosReviewerStrategy strategy;

    @Before
    public void setup() {
        strategy = new SosReviewerStrategy();
    }

    @Test
    public void givenNullLastReviewer_returnTravelAdmin() {
        TravelRole actual = strategy.after(null);
        TravelRole expected = TravelRole.TRAVEL_ADMIN;
        assertEquals(expected, actual);
    }

    @Test
    public void givenTravelAdminLastReviewer_returnNone() {
        TravelRole actual = strategy.after(TravelRole.TRAVEL_ADMIN);
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
    public void givenMajLastReviewer_throwException() {
        strategy.after(TravelRole.MAJORITY_LEADER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenDeptHdLastReviewer_throwException() {
        strategy.after(TravelRole.DEPARTMENT_HEAD);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenSosLastReviewer_throwException() {
        strategy.after(TravelRole.SECRETARY_OF_THE_SENATE);
    }
}
