package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class SqlAttendanceDaoTest extends BaseTest
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
