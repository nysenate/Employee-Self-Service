package gov.nysenate.ess.time.service.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.shiro.authz.Permission;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Category(SillyTest.class)
public class EssTimeSupervisorPermissionFactoryTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EssTimeSupervisorPermissionFactoryTest.class);

    @Autowired private EssTimeSupervisorPermissionFactory supPermFactory;
    @Autowired private EmployeeInfoService empInfoService;

    @Test
    public void getPermissions() throws Exception {
        int empId = 1162;
        Employee employee = empInfoService.getEmployee(empId);
        ImmutableList<Permission> permissions =
                supPermFactory.getPermissions(employee, ImmutableSet.of());
        logger.info("yep");
    }

}