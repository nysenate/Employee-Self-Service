package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class AttendanceDaoTest extends BaseTest
{

    private static final Logger logger = LoggerFactory.getLogger(AttendanceDaoTest.class);

    @Autowired private AttendanceDao attendanceDao;
    @Autowired private PayPeriodService periodService;


    @Test
    public void getAttRecTest() {
        int empId = 11423;
        PayPeriod period = periodService.getPayPeriod(AF, LocalDate.of(2018, 4, 18));
        AttendanceRecord attendanceRecord = attendanceDao.getAttendanceRecord(empId, period);
        BigDecimal expectedWorkHours = new BigDecimal("70");
        BigDecimal workHours = attendanceRecord.getWorkHours().orElse(ZERO);
        assertEquals("Record must have correct work hours", expectedWorkHours, workHours);
    }

    @Test(expected = AttendanceRecordNotFoundEx.class)
    public void getNonExistentAccRecTest() {
        int empId = 11423;
        PayPeriod period = periodService.getPayPeriod(AF, LocalDate.of(2012, 4, 18));
        attendanceDao.getAttendanceRecord(empId, period);
    }
}
