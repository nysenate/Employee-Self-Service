package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.alert.AlertInfo;
import gov.nysenate.ess.core.model.alert.ContactOptions;
import gov.nysenate.ess.core.model.personnel.Employee;

public class AlertInfoView implements ViewObject {

    private int empId;

    private String workPhone;

    private String homePhone;
    private String mobilePhone;
    private String alternatePhone;

    private String mobileOptions;

    private String alternateOptions;

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

        this.mobileOptions = alertInfo.getMobileOptions().getJsString();

        this.alternateOptions = alertInfo.getAlternateOptions().getJsString();

        this.workEmail = employee.getEmail();
        this.personalEmail = alertInfo.getPersonalEmail();
        this.alternateEmail = alertInfo.getAlternateEmail();
    }

    public AlertInfo toAlertInfo() {
        return AlertInfo.builder()
                .setEmpId(empId)
                .setHomePhone(homePhone)
                .setMobilePhone(mobilePhone)
                .setAlternatePhone(alternatePhone)
                .setMobileOptions(ContactOptions.fromJsString(mobileOptions))
                .setAlternateOptions(ContactOptions.fromJsString(alternateOptions))
                .setPersonalEmail(personalEmail)
                .setAlternateEmail(alternateEmail)
                .build();
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

    public String getMobileOptions() {
        return mobileOptions;
    }

    public String getAlternateOptions() {
        return alternateOptions;
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
