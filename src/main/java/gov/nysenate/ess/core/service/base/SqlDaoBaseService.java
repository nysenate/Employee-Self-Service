package gov.nysenate.ess.core.service.base;

import gov.nysenate.ess.core.dao.period.SqlPayPeriodDao;
import gov.nysenate.ess.core.dao.personnel.SqlEmployeeDao;
import gov.nysenate.ess.core.dao.transaction.SqlEmpTransactionDao;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SqlDaoBaseService
{
    @Autowired protected SqlEmpTransactionDao empTransactionDao;
    @Autowired protected SqlPayPeriodDao payPeriodDao;
    @Autowired protected SqlEmployeeDao employeeDao;
}