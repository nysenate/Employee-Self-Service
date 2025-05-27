package gov.nysenate.ess.core.dao.location;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.base.LocationService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class LocationSearchIT extends BaseTest {

    @Autowired private LocationService locationService;

    @Test
    public void givenExactMatch_returnLocation() {
        String term = "A42FB";
        List<Location> results = locationService.searchLocations(term);
        assertEquals(1, results.size());
        assertEquals(term, results.get(0).getLocId().getCode());
    }

    @Test
    public void notCaseSensitive() {
        String term = "a42fb";
        List<Location> results = locationService.searchLocations(term);
        assertEquals(1, results.size());
        assertEquals("A42FB", results.get(0).getLocId().getCode());
    }

    @Test
    public void givenNull_returnEmptyCollection() {
        String term = null;
        List<Location> results = locationService.searchLocations(term);
        assertTrue(results.isEmpty());
    }

    @Test
    public void givenPartialMatch_returnMultiple() {
        String term = "A4";
        List<Location> results = locationService.searchLocations(term);
        assertTrue(results.size() > 1);
        assertResultsMatchTerm(results, term);

        term = "2";
        results = locationService.searchLocations(term);
        assertTrue(results.size() > 1);
        assertResultsMatchTerm(results, term);
    }

    private void assertResultsMatchTerm(List<Location> results, String term) {
        for (Location loc : results) {
            assertTrue(loc.getLocId().getCode().contains(term));
        }
    }
}
