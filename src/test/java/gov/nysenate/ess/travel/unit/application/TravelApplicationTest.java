package gov.nysenate.ess.travel.unit.application;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.amendment.Version;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelApplicationTest {

    private Amendment createAmendment(Version v) {
        return new Amendment.Builder()
                .withVersion(v)
                .build();
    }
}
