package gov.nysenate.ess.core.service.alert;

import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.InvalidAlertInfoEx;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static gov.nysenate.ess.core.model.alert.AlertInfoErrorCode.*;

public abstract class AlertInfoValidation {

    private static final Predicate<String> phoneNumberPredicate = Pattern.compile("^\\d{10}$").asPredicate();
    private static final EmailValidator emailValidator = EmailValidator.getInstance(false);

    /**
     * Validates the given {@link AlertInfo}, throwing a
     * {@link InvalidAlertInfoEx} if invalid data is detected.
     */
    public static void validateAlertInfo(AlertInfo alertInfo, Employee employee) throws InvalidAlertInfoEx {
        validateEmpId(alertInfo);
        validateContactOptions(alertInfo);
        validatePhoneNumbers(alertInfo, employee);
        validateEmails(alertInfo, employee);
    }

    /* --- Internal Methods --- */

    /**
     * Ensures that the alert info has a valid emp id
     */
    private static void validateEmpId(AlertInfo alertInfo) {
        if (alertInfo.getEmpId() <= 0)
            throw new InvalidAlertInfoEx(INVALID_EMP_ID, String.valueOf(alertInfo.getEmpId()), alertInfo);
    }

    /**
     * Ensures that the alert info has a non null mobile contact options
     */
    private static void validateContactOptions(AlertInfo alertInfo) {
        if (alertInfo.getMobileOptions() == null || alertInfo.getAlternateOptions() == null)
            throw new InvalidAlertInfoEx(NULL_CONTACT_OPTIONS, null, alertInfo);
    }

    /**
     * Ensures that all phone numbers are valid with no duplicates
     */
    private static void validatePhoneNumbers(AlertInfo alertInfo, Employee employee) {
        List<String> phoneNumbers = Arrays.asList(
                employee.getWorkPhone(),
                alertInfo.getHomePhone(),
                alertInfo.getAlternatePhone(),
                alertInfo.getMobilePhone()
        );
        Set<String> phoneNumberSet = new HashSet<>();
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumber == null)
                continue;
            String formattedPhoneNumber = phoneNumber.replaceAll("[^0-9]+", "");
            if (!phoneNumberPredicate.test(formattedPhoneNumber))
                throw new InvalidAlertInfoEx(INVALID_PHONE_NUMBER, formattedPhoneNumber, alertInfo);
            if (!phoneNumberSet.add(formattedPhoneNumber))
                throw new InvalidAlertInfoEx(DUPLICATE_PHONE_NUMVER, formattedPhoneNumber, alertInfo);
        }
    }


    /**
     * Ensures that all email addresses are valid with no duplicates
     */
    private static void validateEmails(AlertInfo alertInfo, Employee employee) {
        List<String> emails = Arrays.asList(
                employee.getEmail(),
                alertInfo.getPersonalEmail(),
                alertInfo.getAlternateEmail()
        );
        Set<String> emailSet = new HashSet<>();
        for (String email : emails) {
            if (email == null)
                continue;
            if (!emailValidator.isValid(email))
                throw new InvalidAlertInfoEx(INVALID_EMAIL, email, alertInfo);
            String formattedEmail = StringUtils.lowerCase(email);
            if (!emailSet.add(formattedEmail))
                throw new InvalidAlertInfoEx(DUPLICATE_EMAIL, formattedEmail, alertInfo);
        }
    }
}
