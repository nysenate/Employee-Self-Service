package gov.nysenate.ess.core.model.alert;

/**
 * An exception thrown when data in an {@link AlertInfo} is found to be invalid.
 */
public class InvalidAlertInfoEx extends RuntimeException {

    private AlertInfo alertInfo;
    private AlertInfoErrorCode errorCode;
    private String errorData;

    public InvalidAlertInfoEx(AlertInfoErrorCode errorCode, String errorData, AlertInfo alertInfo) {
        super("Invalid alert for employee " + alertInfo.getEmpId() + ": " + errorCode.getDesc() + " " + errorData);
        this.alertInfo = alertInfo;
        this.errorCode = errorCode;
        this.errorData = errorData;
    }

    public AlertInfo getAlertInfo() {
        return alertInfo;
    }

    public AlertInfoErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorData() {
        return errorData;
    }
}
