package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.BaseTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.time.LocalDate;

@Category(SillyTest.class)
public class SqlRemoteRecordDaoTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDaoTest.class);

    @Resource
    private SqlTimeRecordDao sqlTimeRecordDao;

    @Test
    public void getRecordByEmployeeId() throws Exception {
        logger.info(
            OutputUtils.toJson(sqlTimeRecordDao.getRecordsDuring(10976, Range.closed(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 2, 1))
            )));
    }
}
