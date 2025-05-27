package gov.nysenate.ess.core.dao.personnel;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.dao.personnel.rch.SqlResponsibilityHeadDao;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class SqlResponsibilityHeadDaoIT extends BaseTest {

    @Autowired
    private SqlResponsibilityHeadDao rchDao;

    @Test (expected = IllegalArgumentException.class)
    public void givenNullRchCode_throwsIllegalArgumentException() {
       rchDao.rchForCode(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void givenEmptyRchCode_throwsIllegalArgumentException() {
        rchDao.rchForCode("");
    }

    @Test
    public void givenRchCode_returnRch() {
        ResponsibilityHead rch = rchDao.rchForCode("STSBAC");
        assertEquals("STSBAC", rch.getCode());
        assertEquals("STS/BAC", rch.getShortName());
        assertEquals("STS/Business Applications Center", rch.getName());
        assertEquals("OTH", rch.getAffiliateCode());
        assertTrue(rch.isActive());
    }

    @Test (expected = EmptyResultDataAccessException.class)
    public void givenInvalidRchCode_throwsEmptyResultException() {
        rchDao.rchForCode("ZZZZZZZZZZ");
    }

    @Test
    public void givenManyRchCodes_returnRchs() {
        List<ResponsibilityHead> rchs = rchDao.rchsForCodes(Lists.newArrayList("STS", "STSBAC"));
        assertEquals(2, rchs.size());
    }

    @Test
    public void givenEmptyRchCodesList_returnEmptyList() {
        List<ResponsibilityHead> rchs = rchDao.rchsForCodes(new ArrayList<>());
        assertTrue(rchs.isEmpty());
    }

    @Test
    public void givenManyInvalidRchCodes_returnEmptyList() {
        List<ResponsibilityHead> rchs = rchDao.rchsForCodes(Lists.newArrayList("ZZZZZZZZ", "XXXXXXXXXX"));
        assertTrue(rchs.isEmpty());
    }

    @Test
    public void rchCodeSearchTest() {
        String term = "STS";
        PaginatedList<ResponsibilityHead> results = rchDao.rchSearch(term, LimitOffset.ALL, SortOrder.NONE);
        assertTrue(results.getResults().size() > 0);
        results.getResults().forEach(rch -> assertTrue(
                rch.getCode().toUpperCase().contains(term.toUpperCase()) ||
                        rch.getName().toUpperCase().contains(term.toUpperCase())));
    }
}
