package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.ListMultimap;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SqlAttendanceDaoTest extends BaseTests
{

    private static final Logger logger = LoggerFactory.getLogger(SqlAttendanceDaoTest.class);

    @Autowired
    SqlAttendanceDao attendanceDao;

    @Test
    public void getOpenAttRecsTest() {
//        ListMultimap<Integer, AttendanceRecord> openAttendanceRecords = attendanceDao.getOpenAttendanceRecords();
//        logger.info("got {} records for {} employees", openAttendanceRecords.size(), openAttendanceRecords.keySet().size());
    }
}
