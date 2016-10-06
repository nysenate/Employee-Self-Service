package gov.nysenate.ess.time.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.io.Serializable;

public class TimeRecordErrorException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3297982830752113617L;

    private static final ViewObject defaultErrorData = new ViewObject() {
        @Override
        public String getViewType() {
            return "empty-error-data";
        }
    };

    private TimeRecordErrorCode code;
    private ViewObject errorData;

    public TimeRecordErrorException(TimeRecordErrorCode code, ViewObject errorData) {
        super(code.getMessage());
        this.code = code;
        this.errorData = errorData;
    }

    public TimeRecordErrorException(TimeRecordErrorCode code) {
        this(code, defaultErrorData);
    }

    public TimeRecordErrorCode getCode() {
        return code;
    }

    public ViewObject getErrorData() {
        return errorData;
    }
}
