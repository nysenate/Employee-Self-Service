package gov.nysenate.ess.core.service.base;

import gov.nysenate.ess.web.dao.attendance.SqlTimeRecordDao;
import gov.nysenate.ess.core.dao.period.SqlPayPeriodDao;
import gov.nysenate.ess.web.dao.personnel.SqlSupervisorDao;
import gov.nysenate.ess.core.dao.transaction.SqlEmpTransactionDao;
import gov.nysenate.ess.web.dao.accrual.SqlAccrualDao;
import gov.nysenate.ess.core.dao.personnel.SqlEmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SqlDaoBackedService
{
    @Autowired protected SqlEmpTransactionDao empTransactionDao;
    @Autowired protected SqlPayPeriodDao payPeriodDao;
    @Autowired protected SqlTimeRecordDao timeRecordDao;
    @Autowired protected SqlSupervisorDao supervisorDao;
    @Autowired protected SqlEmployeeDao employeeDao;
    @Autowired protected SqlAccrualDao accrualDao;
}