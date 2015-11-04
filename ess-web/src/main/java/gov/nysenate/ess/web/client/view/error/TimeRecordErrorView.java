package gov.nysenate.ess.web.client.view.error;

import gov.nysenate.ess.web.client.view.base.ViewObject;
import gov.nysenate.ess.web.service.attendance.validation.TimeRecordErrorCode;

public class TimeRecordErrorView implements ViewObject
{

    protected int errorCode;
    protected String errorName;
    protected String errorMessage;
    protected ViewObject errorData;

    public TimeRecordErrorView(TimeRecordErrorCode code, ViewObject errorData) {
        if (code != null) {
            this.errorCode = code.getCode();
            this.errorName = code.name();
            this.errorMessage = code.getMessage();
        }
        this.errorData = errorData;
    }

    @Override
    public String getViewType() {
        return "time record error";
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorName() {
        return errorName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ViewObject getErrorData() {
        return errorData;
    }
}
