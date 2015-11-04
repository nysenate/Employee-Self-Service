package gov.nysenate.ess.web.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.BaseTests;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class EmployeeInfoServiceTests extends BaseTests
{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeInfoServiceTests.class);

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
