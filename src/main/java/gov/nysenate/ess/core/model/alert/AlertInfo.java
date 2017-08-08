package gov.nysenate.ess.core.model.alert;

import com.google.common.base.Objects;

/**
 * Contains contact info for an employee to be used in case of an emergency
 */
public class AlertInfo {

    private int empId;

    private String homePhone;
    private String mobilePhone;
    private String alternatePhone;

    private MobileContactOptions mobileOptions = MobileContactOptions.EVERYTHING;

    private String personalEmail;
    private String alternateEmail;


    private AlertInfo() {}

    /* --- Overrides --- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertInfo)) return false;
        AlertInfo that = (AlertInfo) o;
        return empId == that.empId &&
                mobileOptions == that.mobileOptions &&
                Objects.equal(homePhone, that.homePhone) &&
                Objects.equal(mobilePhone, that.mobilePhone) &&
                Objects.equal(alternatePhone, that.alternatePhone) &&
                Objects.equal(personalEmail, that.personalEmail) &&
                Objects.equal(alternateEmail, that.alternateEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(empId, homePhone, mobilePhone, alternatePhone,
                mobileOptions, personalEmail, alternateEmail);
    }

    /* --- Builder --- */

    public static AlertInfoBuilder builder() {
        return new AlertInfoBuilder();
    }

    public static class AlertInfoBuilder {

        private AlertInfo alertInfo;

        private AlertInfoBuilder() {
            alertInfo = new AlertInfo();
        }

        public AlertInfo build() {
            return alertInfo;
        }

        public AlertInfoBuilder setEmpId(int empId) {
            alertInfo.empId = empId;
            return this;
        }

        public AlertInfoBuilder setHomePhone(String homePhone) {
            alertInfo.homePhone = homePhone;
            return this;
        }

        public AlertInfoBuilder setMobilePhone(String mobilePhone) {
            alertInfo.mobilePhone = mobilePhone;
            return this;
        }

        public AlertInfoBuilder setAlternatePhone(String alternatePhone) {
            alertInfo.alternatePhone = alternatePhone;
            return this;
        }

        public AlertInfoBuilder setMobileOptions(MobileContactOptions mobileOptions) {
            alertInfo.mobileOptions = mobileOptions;
            return this;
        }

        public AlertInfoBuilder setPersonalEmail(String personalEmail) {
            alertInfo.personalEmail = personalEmail;
            return this;
        }

        public AlertInfoBuilder setAlternateEmail(String alternateEmail) {
            alertInfo.alternateEmail = alternateEmail;
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

    public MobileContactOptions getMobileOptions() {
        return mobileOptions;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }
}
