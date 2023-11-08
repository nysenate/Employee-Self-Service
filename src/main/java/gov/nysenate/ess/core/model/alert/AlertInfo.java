package gov.nysenate.ess.core.model.alert;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Contains contact info for an employee to be used in case of an emergency
 */
public class AlertInfo {

    private int empId;

    private String homePhone;
    private String mobilePhone;
    private String alternatePhone;

    private ContactOptions mobileOptions = ContactOptions.EVERYTHING;
    private ContactOptions alternateOptions = ContactOptions.EVERYTHING;

    private String personalEmail;
    private String alternateEmail;


    private AlertInfo() {}

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertInfo that)) return false;
        return empId == that.empId &&
                mobileOptions == that.mobileOptions &&
                alternateOptions == that.alternateOptions &&
                Objects.equal(homePhone, that.homePhone) &&
                Objects.equal(mobilePhone, that.mobilePhone) &&
                Objects.equal(alternatePhone, that.alternatePhone) &&
                Objects.equal(personalEmail, that.personalEmail) &&
                Objects.equal(alternateEmail, that.alternateEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(empId, homePhone, mobilePhone, alternatePhone,
                mobileOptions, alternateOptions, personalEmail, alternateEmail);
    }

    /* --- Builder --- */

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final AlertInfo alertInfo = new AlertInfo();

        private Builder() {}

        public AlertInfo build() {
            return alertInfo;
        }

        public Builder setEmpId(int empId) {
            alertInfo.empId = empId;
            return this;
        }

        public Builder setHomePhone(String homePhone) {
            alertInfo.homePhone = formatPhoneNumber(homePhone);
            return this;
        }

        public Builder setMobilePhone(String mobilePhone) {
            alertInfo.mobilePhone = formatPhoneNumber(mobilePhone);
            return this;
        }

        public Builder setAlternatePhone(String alternatePhone) {
            alertInfo.alternatePhone = formatPhoneNumber(alternatePhone);
            return this;
        }

        public Builder setMobileOptions(ContactOptions mobileOptions) {
            alertInfo.mobileOptions = mobileOptions;
            return this;
        }

        public Builder setAlternateOptions(ContactOptions alternateOptions) {
            alertInfo.alternateOptions = alternateOptions;
            return this;
        }

        public Builder setPersonalEmail(String personalEmail) {
            alertInfo.personalEmail = formatEmailAddress(personalEmail);
            return this;
        }

        public Builder setAlternateEmail(String alternateEmail) {
            alertInfo.alternateEmail = formatEmailAddress(alternateEmail);
            return this;
        }

        /**
         * Format a phone number
         * @param phoneNumber a phone number
         * @return the {@code phoneNumber} with all non number characters removed or null if {@code phoneNumber} is null.
         */
        private static String formatPhoneNumber(String phoneNumber) {
            String formattedPhoneNumber = Optional.ofNullable(phoneNumber)
                    .map(phoneNo -> phoneNo.replaceAll("[^0-9]", ""))
                    .orElse(null);
            return StringUtils.isBlank(formattedPhoneNumber)
                    ? null : formattedPhoneNumber;
        }

        /**
         * Format email addresses
         * Convert blank email addresses to null
         * Trim surrounding space from address
         * @param emailAddress String
         * @return String
         */
        private static String formatEmailAddress(String emailAddress) {
            if (StringUtils.isBlank(emailAddress)) {
                return null;
            }
            return emailAddress.trim();
        }

    }

    /* --- Getters --- */

    public int getEmpId() {
        return empId;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public ContactOptions getMobileOptions() {
        return mobileOptions;
    }

    public ContactOptions getAlternateOptions() {
        return alternateOptions;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }
}
