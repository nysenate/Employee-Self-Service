package gov.nysenate.ess.core.dao.location;

import com.google.common.collect.ImmutableCollection;
import gov.nysenate.ess.core.DaoTests;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.model.unit.Location;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocationSearchTests extends DaoTests {

    private static final Logger logger = LoggerFactory.getLogger(LocationSearchTests.class);

    @Autowired private LocationDao locationDao;

    @Test
    public void givenExactMatch_returnLocation() {
        String term = "A42FB";
        ImmutableCollection<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() == 1);
        assertEquals(term, results.asList().get(0).getLocId().getCode());
    }

    @Test
    public void notCaseSensitive() {
        String term = "a42fb";
        ImmutableCollection<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() == 1);
        assertEquals("A42FB", results.asList().get(0).getLocId().getCode());
    }

    @Test
    public void givenNull_returnEmptyCollection() {
        String term = null;
        ImmutableCollection<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() == 0);
    }

    @Test
    public void givenPartialMatch_returnMultiple() {
        String term = "A4";
        ImmutableCollection<Location> results = locationDao.searchLocations(term);
        assertTrue(results.size() > 1);
        assertResultsMatchTerm(results, term);

        term = "2";
        results = locationDao.searchLocations(term);
        assertTrue(results.size() > 1);
        assertResultsMatchTerm(results, term);
    }

    private void assertResultsMatchTerm(ImmutableCollection<Location> results, String term) {
        for (Location loc : results) {
            assertTrue(loc.getLocId().getCode().contains(term));
        }
    }
}
