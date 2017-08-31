package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.alert.InvalidAlertInfoEx;

public class InvalidAlertInfoView implements ViewObject {

    private String reason;
    private AlertInfoView alertInfo;

    public InvalidAlertInfoView(InvalidAlertInfoEx ex, Employee emp) {
        this.reason = ex.getMessage();
        this.alertInfo = new AlertInfoView(ex.getAlertInfo(), emp);
    }

    public String getReason() {
        return reason;
    }

    public AlertInfoView getAlertInfo() {
        return alertInfo;
    }

    @Override
    public String getViewType() {
        return "invalid-alert-info";
    }
}
