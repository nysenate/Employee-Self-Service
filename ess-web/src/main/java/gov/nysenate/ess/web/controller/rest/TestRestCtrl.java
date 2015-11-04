package gov.nysenate.ess.web.controller.rest;

import gov.nysenate.ess.core.dao.period.PayPeriodDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.web.dao.personnel.SupervisorDao;
import gov.nysenate.ess.web.dao.accrual.AccrualDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestCtrl extends BaseRestCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TestRestCtrl.class);

    @Autowired private SupervisorDao supervisorDao;
    @Autowired private EmployeeDao employeeDao;
    @Autowired private AccrualDao accrualDao;
    @Autowired private PayPeriodDao payPeriodDao;
}