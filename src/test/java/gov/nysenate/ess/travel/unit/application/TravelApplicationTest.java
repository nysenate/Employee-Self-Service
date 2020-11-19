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

    private Amendment createAmendment(Version v) {
        return new Amendment.Builder()
                .withVersion(v)
                .build();
    }
}
