package gov.nysenate.ess.time.service.payroll;

import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.dao.payroll.SqlPaycheckDao;
import gov.nysenate.ess.time.model.payroll.Paycheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EssPaycheckService implements  PaycheckService
{
    @Autowired
    SqlPaycheckDao paycheckDao;

    @Override
    public List<Paycheck> getEmployeePaychecksForYear(int empId, int year) {
        return paycheckDao.getEmployeePaychecksForYear(empId, year);
    }

    @Override
    public List<Paycheck> getEmployeePaychecksForFiscalYear(int empId, int endYear) {
        return paycheckDao.getEmployeePaychecksForDates(empId, DateUtils.fiscalYearDateRange(endYear));
    }
}
