package gov.nysenate.ess.core.service.alert;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.InvalidAlertInfoEx;
import gov.nysenate.ess.core.model.alert.ContactOptions;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
public class AlertInfoValidationServiceIT extends BaseTest {

    private static final int empId = 11423;
    private static final String homePhone = "1111111111";
    private static final String altPhone = "2222222222";
    private static final String mobilePhone = "3333333333";
    private static final String invalidPhone = "33333333334";
    private static final String personalEmail = "test@test.com";
    private static final String altEmail = "test2@test.com";
    private static final String altEmail_dupe = "TEST2@test.com";
    private static final String invalidEmail = "test2@@test.com";

    @Autowired private AlertInfoValidationService alertInfoValidationService;
    @Autowired private EmployeeInfoService employeeInfoService;

    private AlertInfo.Builder alertInfoBldr;
    private String workPhone;
    private String workEmail;

    @Before
    public void setUp() throws Exception {
        Employee employee = employeeInfoService.getEmployee(empId);
        workPhone = employee.getWorkPhone();
        workEmail = employee.getEmail();
        alertInfoBldr = AlertInfo.builder()
                .setEmpId(empId)
                .setHomePhone(homePhone)
                .setAlternatePhone(altPhone)
                .setMobilePhone(mobilePhone)
                .setMobileOptions(ContactOptions.EVERYTHING)
                .setPersonalEmail(personalEmail)
                .setAlternateEmail(altEmail);
    }

    @Test
    public void validateAlertInfo_valid() {
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_nullOptions() {
        alertInfoBldr.setMobileOptions(null);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_invalidPhone() {
        alertInfoBldr.setAlternatePhone(invalidPhone);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_duplicatePhone() {
        alertInfoBldr.setMobilePhone(homePhone);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_duplicateWorkPhone() {
        alertInfoBldr.setAlternatePhone(workPhone);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_invalidEmail() {
        alertInfoBldr.setPersonalEmail(invalidEmail);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_duplicateEmail() {
        alertInfoBldr.setPersonalEmail(altEmail_dupe);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void validateAlertInfo_duplicateWorkEmail() {
        alertInfoBldr.setPersonalEmail(workEmail);
        validate();
    }

    /* --- Internal Methods --- */

    /**
     * Convenience method to run the validator on the result of the current alert info builder
     */
    private void validate() {
        alertInfoValidationService.validateAlertInfo(alertInfoBldr.build());
    }

}