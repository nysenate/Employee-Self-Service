package gov.nysenate.ess.time.service.allowance;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.time.model.allowances.AllowanceUsage;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Category(SillyTest.class)
public class EmpsOverAllowanceTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(EmpsOverAllowanceTest.class);

    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private AllowanceService allowanceService;

    @Test
    public void empsOverAllowanceTest() {
        List<Employee> tempEmps = employeeInfoService.getAllEmployees(true).stream()
                .filter(emp -> emp.getPayType() == PayType.TE)
                .sorted(Comparator.comparing(Employee::getEmployeeId))
                .collect(Collectors.toList());

        logger.info("Looking for emps over their yearly allowance..");

        int count = 0;

        for (Employee employee : tempEmps) {
            int year = LocalDateTime.now().getYear();
            AllowanceUsage allowanceUsage = allowanceService.getAllowanceUsage(employee.getEmployeeId(), year);
            BigDecimal yearlyAllowance = allowanceUsage.getYearlyAllowance();
            BigDecimal moneyUsed = allowanceUsage.getMoneyUsed();
            if (yearlyAllowance.compareTo(moneyUsed) < 0) {
                count++;
                logger.info("{} {} {} - over by {}",
                        employee.getEmployeeId(),
                        employee.getFullName(),
                        employee.getEmail(),
                        moneyUsed.subtract(yearlyAllowance));
            }
        }

        logger.info("Found {} emps over yearly allowance", count);
    }
}