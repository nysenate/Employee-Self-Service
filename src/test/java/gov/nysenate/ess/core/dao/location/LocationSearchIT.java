package gov.nysenate.ess.core.dao.location;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.model.unit.Location;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class LocationSearchIT extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LocationSearchIT.class);

    @Autowired private LocationDao locationDao;

    @Test
    public void givenExactMatch_returnLocation() {
        String term = "A42FB";
        List<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() == 1);
        assertEquals(term, results.get(0).getLocId().getCode());
    }

    @Test
    public void notCaseSensitive() {
        String term = "a42fb";
        List<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() == 1);
        assertEquals("A42FB", results.get(0).getLocId().getCode());
    }

    @Test
    public void givenNull_returnEmptyCollection() {
        String term = null;
        List<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() == 0);
    }

    @Test
    public void givenPartialMatch_returnMultiple() {
        String term = "A4";
        List<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() > 1);
        assertResultsMatchTerm(results, term);

        term = "2";
        results = locationDao.searchLocations(term);
        assertTrue(results.size() > 1);
        assertResultsMatchTerm(results, term);
    }

    private void assertResultsMatchTerm(List<Location> results, String term) {
        for (Location loc : results) {
            assertTrue(loc.getLocId().getCode().contains(term));
        }
    }
}
