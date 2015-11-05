package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.io.Serializable;

public class TimeRecordErrorException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3297982830752113617L;

    private TimeRecordErrorCode code;
    private ViewObject errorData;

    public TimeRecordErrorException(TimeRecordErrorCode code, ViewObject errorData) {
        super(code.getMessage());
        this.code = code;
        this.errorData = errorData;
    }

    public TimeRecordErrorException(TimeRecordErrorCode code) {
        this(code, () -> "empty error data");
    }

    public TimeRecordErrorCode getCode() {
        return code;
    }

    public ViewObject getErrorData() {
        return errorData;
    }
}
