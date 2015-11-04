package gov.nysenate.ess.web.model.attendance;

import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.Assert.*;

@ProperTest
public class TimeRecordScopeTests
{
    private static final Logger logger = LoggerFactory.getLogger(TimeRecordScopeTests.class);

    @Test
    public void testGetStatuses() throws Exception {
        Set<TimeRecordStatus> empStatuses = TimeRecordStatus.unlockedForEmployee();

        Assert.assertNotNull(empStatuses);
        Assert.assertTrue(empStatuses.contains(TimeRecordStatus.NOT_SUBMITTED));
        Assert.assertTrue(empStatuses.contains(TimeRecordStatus.DISAPPROVED));
        Assert.assertTrue(empStatuses.contains(TimeRecordStatus.DISAPPROVED_PERSONNEL));

        logger.info("Employee scope statuses {}", TimeRecordScope.EMPLOYEE.getStatuses());
        assertEquals(empStatuses, TimeRecordScope.EMPLOYEE.getStatuses());
        logger.info("Supervisor scope statuses {}", TimeRecordScope.SUPERVISOR.getStatuses());
        assertEquals(TimeRecordStatus.unlockedForSupervisor(), TimeRecordScope.SUPERVISOR.getStatuses());
        logger.info("Personnel scope statuses {}", TimeRecordScope.PERSONNEL.getStatuses());
        assertEquals(TimeRecordStatus.unlockedForPersonnel(), TimeRecordScope.PERSONNEL.getStatuses());
    }

    @Test
    public void testGetCode() throws Exception {
        assertEquals("E", TimeRecordScope.EMPLOYEE.getCode());
        assertEquals("S", TimeRecordScope.SUPERVISOR.getCode());
        assertEquals("P", TimeRecordScope.PERSONNEL.getCode());
    }

    @Test
    public void testGetScopeFromCode() throws Exception {
        assertEquals(TimeRecordScope.EMPLOYEE, TimeRecordScope.getScopeFromCode("E"));
        assertEquals(TimeRecordScope.EMPLOYEE, TimeRecordScope.getScopeFromCode("e"));
        assertEquals(TimeRecordScope.EMPLOYEE, TimeRecordScope.getScopeFromCode(" e  "));
        assertEquals(TimeRecordScope.SUPERVISOR, TimeRecordScope.getScopeFromCode(" S"));
        assertEquals(TimeRecordScope.SUPERVISOR, TimeRecordScope.getScopeFromCode(" s  "));
        assertEquals(TimeRecordScope.SUPERVISOR, TimeRecordScope.getScopeFromCode("S"));
        assertEquals(TimeRecordScope.PERSONNEL, TimeRecordScope.getScopeFromCode("P"));
        assertEquals(TimeRecordScope.PERSONNEL, TimeRecordScope.getScopeFromCode(" p "));
        assertEquals(TimeRecordScope.PERSONNEL, TimeRecordScope.getScopeFromCode("p"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetScopeFromCodeFailsWithNoCode() throws Exception {
        TimeRecordScope.getScopeFromCode("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetScopeFromCodeFailsWithBadCode() throws Exception {
        TimeRecordScope.getScopeFromCode("X");
    }
}