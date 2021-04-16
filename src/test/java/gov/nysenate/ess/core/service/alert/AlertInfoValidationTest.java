package gov.nysenate.ess.core.service.alert;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.InvalidAlertInfoEx;
import gov.nysenate.ess.core.model.alert.ContactOptions;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class AlertInfoValidationTest {

    private static final int empId = 11423;
    private static final int badEmpId = -1;
    private static final String workPhone = "3141582652";
    private static final String homePhone = "1111111111";
    private static final String altPhone = "2222222222";
    private static final String workEmail = "test@nysenate.gov";
    private static final String mobilePhone = "3333333333";
    private static final String invalidPhone = "33333333334";
    private static final String personalEmail = "test@test.com";
    private static final String altEmail = "test2@test.com";
    private static final String altEmail_dupe = "TEST2@test.com";
    private static final String invalidEmail = "test2@@test.com";
    private AlertInfo.Builder alertInfoBuilder;

    @Before
    public void setUp() {


        alertInfoBuilder = AlertInfo.builder()
                .setEmpId(empId)
                .setHomePhone(homePhone)
                .setAlternatePhone(altPhone)
                .setAlternateOptions(ContactOptions.EVERYTHING)
                .setMobilePhone(mobilePhone)
                .setMobileOptions(ContactOptions.EVERYTHING)
                .setPersonalEmail(personalEmail)
                .setAlternateEmail(altEmail);
    }

    /**
     * Convenience method to run the validator on the result of the current alert info builder
     */
    public void validate() {
        AlertInfo alertInfo = alertInfoBuilder.build();
        Employee employee = new Employee();
        employee.setEmployeeId(empId);
        employee.setEmail(workEmail);
        employee.setWorkPhone(workPhone);
        AlertInfoValidation.validateAlertInfo(alertInfo, employee);
    }

    @Test
    public void newAlertInfoTest() {
        validate();
    }

    @Test
    public void nullEmailTest() {
        alertInfoBuilder.setAlternateEmail(null);
        validate();
    }

    @Test
    public void nullPhoneNumberTest() {
        alertInfoBuilder.setAlternatePhone(null);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void badEmpIdTest() {
        alertInfoBuilder.setEmpId(badEmpId);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void nullMobileOptionsTest() {
        alertInfoBuilder.setMobileOptions(null);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void invalidPhoneTest() {
        alertInfoBuilder.setAlternatePhone(invalidPhone);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void duplicatePhoneTest() {
        alertInfoBuilder.setMobilePhone(homePhone);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void duplicateWorkPhoneTest() {
        alertInfoBuilder.setAlternatePhone(workPhone);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void invalidEmailTest() {
        alertInfoBuilder.setPersonalEmail(invalidEmail);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void duplicateEmailTest() {
        alertInfoBuilder.setPersonalEmail(altEmail_dupe);
        validate();
    }

    @Test(expected = InvalidAlertInfoEx.class)
    public void duplicateWorkEmailTest() {
        alertInfoBuilder.setPersonalEmail(workEmail);
        validate();
    }
}
