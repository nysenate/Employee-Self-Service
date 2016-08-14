package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.BaseTests;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.time.LocalDate;

public class SqlRemoteRecordDaoTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlRemoteRecordDaoTests.class);

    @Resource
    private SqlTimeRecordDao sqlTimeRecordDao;

    @Test
    public void getRecordByEmployeeId() throws Exception {
        logger.info(
            OutputUtils.toJson(sqlTimeRecordDao.getRecordsDuring(10976, Range.closed(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 2, 1))
            )));
    }
}
