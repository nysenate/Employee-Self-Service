package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.alert.AlertInfoErrorCode;
import gov.nysenate.ess.core.model.alert.InvalidAlertInfoEx;
import gov.nysenate.ess.core.model.personnel.Employee;

public class InvalidAlertInfoView implements ViewObject {

    private AlertInfoErrorCode alertErrorCode;
    private String alertErrorData;
    private AlertInfoView alertInfo;

    public InvalidAlertInfoView(InvalidAlertInfoEx ex, Employee emp) {
        this.alertErrorCode = ex.getErrorCode();
        this.alertErrorData = ex.getErrorData();
        this.alertInfo = new AlertInfoView(ex.getAlertInfo(), emp);
    }

    public AlertInfoErrorCode getAlertErrorCode() {
        return alertErrorCode;
    }

    public String getAlertErrorData() {
        return alertErrorData;
    }

    public AlertInfoView getAlertInfo() {
        return alertInfo;
    }

    @Override
    public String getViewType() {
        return "invalid-alert-info";
    }
}
