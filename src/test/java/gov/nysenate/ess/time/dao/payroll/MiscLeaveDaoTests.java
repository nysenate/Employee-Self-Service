package gov.nysenate.ess.time.dao.payroll;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.core.BaseTests;
import gov.nysenate.ess.time.client.view.MiscLeaveGrantView;
import gov.nysenate.ess.time.model.payroll.MiscLeaveGrant;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class MiscLeaveDaoTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(MiscLeaveDaoTests.class);

    @Autowired MiscLeaveDao miscLeaveDao;

    @Test
    public void miscLeaveTest() {
        List<MiscLeaveGrant> grants = miscLeaveDao.getMiscLeaveGrants(6221);
        logger.info("{}", OutputUtils.toJson(
                grants.stream()
                        .map(MiscLeaveGrantView::new)
                        .collect(Collectors.toList())));
    }
}
