package gov.nysenate.ess.core.service.alert;

import gov.nysenate.ess.core.model.alert.AlertInfo;

/**
 * An exception thrown when data in an {@link AlertInfo} is found to be invalid.
 */
public class InvalidAlertInfoEx extends RuntimeException {

    private AlertInfo alertInfo;

    public InvalidAlertInfoEx(String reason, AlertInfo alertInfo) {
        super("Invalid alert for employee " + alertInfo.getEmpId() + ": " + reason);
        this.alertInfo = alertInfo;
    }

    public AlertInfo getAlertInfo() {
        return alertInfo;
    }
}
