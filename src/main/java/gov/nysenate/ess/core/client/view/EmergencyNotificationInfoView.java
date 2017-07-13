package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.emergency_notification.EmergencyNotificationInfo;
import gov.nysenate.ess.core.model.personnel.Employee;

public class EmergencyNotificationInfoView implements ViewObject {

    private int empId;

    private String workPhone;

    private String homePhone;
    private String mobilePhone;
    private String alternatePhone;

    private boolean smsSubscribed;

    private String workEmail;

    private String personalEmail;
    private String alternateEmail;

    private EmergencyNotificationInfoView() {}

    public EmergencyNotificationInfoView(EmergencyNotificationInfo eni, Employee employee) {
        this.empId = eni.getEmpId();

        this.workPhone = employee.getWorkPhone();

        this.homePhone = eni.getHomePhone();
        this.mobilePhone = eni.getMobilePhone();
        this.alternatePhone = eni.getAlternatePhone();

        this.smsSubscribed = eni.isSmsSubscribed();

        this.workEmail = employee.getEmail();
        this.personalEmail = eni.getPersonalEmail();
        this.alternateEmail = eni.getAlternateEmail();
    }

    public EmergencyNotificationInfo toEmergencyNotificationInfo() {
        return EmergencyNotificationInfo.builder()
                .setEmpId(empId)
                .setHomePhone(homePhone)
                .setMobilePhone(mobilePhone)
                .setAlternatePhone(alternatePhone)
                .setSmsSubscribed(smsSubscribed)
                .setPersonalEmail(personalEmail)
                .setAlternateEmail(alternateEmail)
                .build();
    }

    @Override
    public String getViewType() {
        return "emergency-notification-info";
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

    public boolean isSmsSubscribed() {
        return smsSubscribed;
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
