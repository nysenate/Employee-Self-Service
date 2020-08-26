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

    private boolean mobileCallable;
    private boolean mobileTextable;

    private boolean alternateCallable;
    private boolean alternateTextable;

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

        ContactOptions mobileOptions = alertInfo.getMobileOptions();
        this.mobileCallable = mobileOptions.isCallable();
        this.mobileTextable = mobileOptions.isTextable();

        ContactOptions alternateOptions = alertInfo.getAlternateOptions();
        this.alternateCallable = alternateOptions.isCallable();
        this.alternateTextable = alternateOptions.isTextable();

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
                .setMobileOptions(ContactOptions.getContactOptions(mobileCallable, mobileTextable))
                .setAlternateOptions(ContactOptions.getContactOptions(alternateCallable, alternateTextable))
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

    public boolean isMobileCallable() {
        return mobileCallable;
    }

    public boolean isMobileTextable() {
        return mobileTextable;
    }

    public boolean isAlternateCallable() {
        return alternateCallable;
    }

    public boolean isAlternateTextable() {
        return alternateTextable;
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
