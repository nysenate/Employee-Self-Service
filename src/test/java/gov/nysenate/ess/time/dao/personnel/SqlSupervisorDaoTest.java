package gov.nysenate.ess.time.dao.personnel;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.transaction.TransactionInfo;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.time.model.personnel.SupGrantType;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import gov.nysenate.ess.time.model.personnel.SupervisorOverride;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Category(SillyTest.class)
public class SqlSupervisorDaoTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDaoTest.class);

    @Autowired
    private SupervisorDao supervisorDao;

    @Test
    public void testGetSupEmpGroup_ReturnsEmpGroup() throws Exception {
        SupervisorEmpGroup group =
            supervisorDao.getSupervisorEmpGroup(9896, Range.closed(LocalDate.of(1970, 1, 1), LocalDate.now()));
        logger.info(OutputUtils.toJson(group));
    }

    @Test
    public void issupTest() {
        supervisorDao.isSupervisor(11423, DateUtils.ALL_DATES);
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("{}", supervisorDao.isSupervisor(11423, DateUtils.ALL_DATES));
        logger.info("{}", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void supOverrideTest() throws Exception {
//        supervisorDao.setSupervisorOverride(11423, 7048, Range.closed(LocalDate.of(2015, 7, 16), LocalDate.of(2015, 7, 29)));
        logger.info("{}", OutputUtils.toJson(supervisorDao.getSupervisorOverrides(9896, SupGrantType.GRANTER)));
    }

    @Test
    public void testSetSupervisorOverrides() throws Exception {
        supervisorDao.setSupervisorOverride(9896, 7048, true, null, null);
    }

    @Test
    public void getSupChangesTest() {
        LocalDateTime fromDate = LocalDate.of(2015, 9, 1).atStartOfDay();
        List<TransactionInfo> supChanges = supervisorDao.getSupTransChanges(fromDate);
        logger.info("{}", supChanges.stream().map(TransactionInfo::getEmployeeId).collect(Collectors.toSet()));
    }

    @Test
    public void getSupOvrChangesTest() {
        LocalDateTime fromDate = LocalDate.of(2015, 9, 1).atStartOfDay();
        List<SupervisorOverride> ovrChanges = supervisorDao.getSupOverrideChanges(fromDate);
        logger.info("{}", ovrChanges.size());
    }

    @Test
    public void getLatestSupdateTest() {
        LocalDateTime latestUpdate = supervisorDao.getLastSupUpdateDate();
        logger.info("{}", latestUpdate);
    }
}
