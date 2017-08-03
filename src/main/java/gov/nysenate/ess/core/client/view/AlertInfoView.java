package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.MobileContactOptions;
import gov.nysenate.ess.core.model.personnel.Employee;

public class AlertInfoView implements ViewObject {

    private int empId;

    private String workPhone;

    private String homePhone;
    private String mobilePhone;
    private String alternatePhone;

    private MobileContactOptions mobileOptions;

    private String workEmail;

    private String personalEmail;
    private String alternateEmail;

    private AlertInfoView() {}

    public AlertInfoView(AlertInfo alertInfo, Employee employee) {
        this.empId = alertInfo.getEmpId();

        this.workPhone = employee.getWorkPhone();

        this.homePhone = alertInfo.getHomePhone();
        this.mobilePhone = alertInfo.getMobilePhone();
        this.alternatePhone = alertInfo.getAlternatePhone();

        this.mobileOptions = alertInfo.getMobileOptions();

        this.workEmail = employee.getEmail();
        this.personalEmail = alertInfo.getPersonalEmail();
        this.alternateEmail = alertInfo.getAlternateEmail();
    }

    public AlertInfo toAlertInfo() {
        return AlertInfo.builder()
                .setEmpId(empId)
                .setHomePhone(stripFormatting(homePhone))
                .setMobilePhone(stripFormatting(mobilePhone))
                .setAlternatePhone(stripFormatting(alternatePhone))
                .setMobileOptions(mobileOptions)
                .setPersonalEmail(personalEmail)
                .setAlternateEmail(alternateEmail)
                .build();
    }

    /**
     * Strip all phone number formatting a user may of entered.
     * @param phoneNumber a phone number entered by a user.
     * @return the {@code phoneNumber} with all non number characters removed or null if {@code phoneNumber} is null.
     */
    private String stripFormatting(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.replaceAll("[^0-9]", "");
    }

    @Override
    public String getViewType() {
        return "alert-info";
    }

    public int getEmpId() {
        return empId;
    }

    public String getWorkPhone() {
        return workPhone;
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

    public String getWorkEmail() {
        return workEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getAlternateEmail() {
        return alternateEmail;
    }
}
