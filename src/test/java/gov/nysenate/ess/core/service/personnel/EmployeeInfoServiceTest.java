package gov.nysenate.ess.core.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.OutputUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Category(SillyTest.class)
public class EmployeeInfoServiceTest extends BaseTest
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeInfoServiceTest.class);

    @Autowired
    EmployeeInfoService employeeInfoService;

    @Test
    public void activeDatesTest() {
        RangeSet<LocalDate> activeDates = employeeInfoService.getEmployeeActiveDatesService(1719);
        logger.info("{}", activeDates.asRanges());
    }

    @Test
    public void getEmpInfoTest() {
        Employee emp = employeeInfoService.getEmployee(3562);
        logger.info("{}", OutputUtils.toJson(emp));
    }

    private void printEmpInfoAtDate(int empId, LocalDate date) {
        Employee emp = employeeInfoService.getEmployee(empId, date);
        logger.info("{}", OutputUtils.toJson(emp));
    }

    @Test
    public void empInfoAtDateTest() {
        int empId = 11755;
        LocalDate date = LocalDate.of(2015, 8, 24);
//        LocalDate date = LocalDate.now();
        printEmpInfoAtDate(empId, date);
    }

}
