package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class SqlTimeEntryDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTimeEntryDaoTests.class);

    @Autowired
    private SqlTimeEntryDao remoteTimeEntryDao;

    public static TimeEntry testEntry;
    public static TimeEntry otherTestEntry;

    public static void testEntryInit() {
        testEntry = new TimeEntry();
        testEntry.setEntryId(new BigInteger("11111111111111111111111111111111111111"));
        testEntry.setTimeRecordId(new BigInteger("11111111111111111111111111111111111111"));
        testEntry.setEmpId(11423);
        testEntry.setEmployeeName("STOUFFER");
        testEntry.setDate(LocalDate.of(1990, 8, 14));
//        testEntry.setWorkHours(10);
//        testEntry.setTravelHours(0);
//        testEntry.setHolidayHours(0);
//        testEntry.setVacationHours(0);
//        testEntry.setPersonalHours(0);
//        testEntry.setSickEmpHours(0);
//        testEntry.setSickFamHours(0);
//        testEntry.setMiscHours(0);
        testEntry.setMiscType(MiscLeaveType.valueOfCode(null));
        testEntry.setOriginalUserId("STOUFFER");
        testEntry.setUpdateUserId("STOUFFER");
        testEntry.setOriginalDate(LocalDate.of(1990, 8, 14).atStartOfDay());
        testEntry.setUpdateDate(LocalDate.of(1990, 8, 14).atStartOfDay());
        testEntry.setActive(true);
        testEntry.setAccruing(true);
        testEntry.setEmpComment("was born today");
        testEntry.setPayType(PayType.RA);

        otherTestEntry = new TimeEntry();
        otherTestEntry.setEntryId(new BigInteger("11111111111111111111111111111111111112"));
        otherTestEntry.setTimeRecordId(new BigInteger("11111111111111111111111111111111111111"));
        otherTestEntry.setEmpId(11423);
        otherTestEntry.setEmployeeName("STOUFFER");
        otherTestEntry.setDate(LocalDate.of(1990, 8, 15));
//        otherTestEntry.setWorkHours(10);
//        otherTestEntry.setTravelHours(0);
//        otherTestEntry.setHolidayHours(0);
//        otherTestEntry.setVacationHours(0);
//        otherTestEntry.setPersonalHours(2);
//        otherTestEntry.setSickEmpHours(0);
//        otherTestEntry.setSickFamHours(0);
//        otherTestEntry.setMiscHours(12);
        otherTestEntry.setMiscType(MiscLeaveType.MILITARY_LEAVE);
        otherTestEntry.setOriginalUserId("STOUFFER");
        otherTestEntry.setUpdateUserId("STOUFFER");
        otherTestEntry.setOriginalDate(LocalDate.of(1990, 8, 15).atStartOfDay());
        otherTestEntry.setUpdateDate(LocalDate.of(1990, 8, 15).atStartOfDay());
        otherTestEntry.setActive(true);
        otherTestEntry.setAccruing(true);
        otherTestEntry.setEmpComment(null);
        otherTestEntry.setPayType(PayType.RA);
    }

    @PostConstruct
    private void init(){
        testEntryInit();
    }

    @Test
    public void updateTimeEntryTest(){
        remoteTimeEntryDao.updateTimeEntry(testEntry);
    }

    @Test
    public void getTimeEntryTest(){
        remoteTimeEntryDao.updateTimeEntry(otherTestEntry);
        try {
            TimeEntry timeEntry = remoteTimeEntryDao.getTimeEntryById(otherTestEntry.getEntryId());
            assert (timeEntry.equals(otherTestEntry));
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }

    @Test
    public void getTimeEntriesTest(){
        remoteTimeEntryDao.updateTimeEntry(testEntry);
        remoteTimeEntryDao.updateTimeEntry(otherTestEntry);
        try {
            List<TimeEntry> timeEntry = remoteTimeEntryDao.getTimeEntriesByRecordId(testEntry.getTimeRecordId());
            assert (timeEntry.contains(testEntry));
            assert (timeEntry.contains(otherTestEntry));
        }
        catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }
}
