package gov.nysenate.ess.core.model.emergency_notification;

import com.google.common.base.Objects;

/**
 * Contains contact info for an employee to be used in case of an emergency
 */
public class EmergencyNotificationInfo {

    private int empId;

    private String homePhone;
    private String mobilePhone;
    private String alternatePhone;

    private boolean smsSubscribed;

    private String personalEmail;
    private String alternateEmail;


    private EmergencyNotificationInfo() {}

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmergencyNotificationInfo)) return false;
        EmergencyNotificationInfo that = (EmergencyNotificationInfo) o;
        return empId == that.empId &&
                smsSubscribed == that.smsSubscribed &&
                Objects.equal(homePhone, that.homePhone) &&
                Objects.equal(mobilePhone, that.mobilePhone) &&
                Objects.equal(alternatePhone, that.alternatePhone) &&
                Objects.equal(personalEmail, that.personalEmail) &&
                Objects.equal(alternateEmail, that.alternateEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(empId, homePhone, mobilePhone, alternatePhone,
                smsSubscribed, personalEmail, alternateEmail);
    }

    /* --- Builder --- */

    public static EmergencyNotificationInfoBuilder builder() {
        return new EmergencyNotificationInfoBuilder();
    }

    public static class EmergencyNotificationInfoBuilder {

        private EmergencyNotificationInfo eni;

        private EmergencyNotificationInfoBuilder() {
            eni = new EmergencyNotificationInfo();
        }

        public EmergencyNotificationInfo build() {
            return eni;
        }

        public EmergencyNotificationInfoBuilder setEmpId(int empId) {
            eni.empId = empId;
            return this;
        }

        public EmergencyNotificationInfoBuilder setHomePhone(String homePhone) {
            eni.homePhone = homePhone;
            return this;
        }

        public EmergencyNotificationInfoBuilder setMobilePhone(String mobilePhone) {
            eni.mobilePhone = mobilePhone;
            return this;
        }

        public EmergencyNotificationInfoBuilder setAlternatePhone(String alternatePhone) {
            eni.alternatePhone = alternatePhone;
            return this;
        }

        public EmergencyNotificationInfoBuilder setSmsSubscribed(boolean smsSubscribed) {
            eni.smsSubscribed = smsSubscribed;
            return this;
        }

        public EmergencyNotificationInfoBuilder setPersonalEmail(String personalEmail) {
            eni.personalEmail = personalEmail;
            return this;
        }

        public EmergencyNotificationInfoBuilder setAlternateEmail(String alternateEmail) {
            eni.alternateEmail = alternateEmail;
            return this;
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

    public boolean isSmsSubscribed() {
        return smsSubscribed;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }
}
