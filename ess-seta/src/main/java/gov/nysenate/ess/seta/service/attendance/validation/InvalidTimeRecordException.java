package gov.nysenate.ess.seta.service.attendance.validation;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;

import java.io.Serializable;
import java.util.Map;

public class InvalidTimeRecordException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -9191905661476035241L;

    private TimeRecord timeRecord;
    private Map<TimeRecordErrorCode, ViewObject> detectedErrors;

    public InvalidTimeRecordException(TimeRecord timeRecord, Map<TimeRecordErrorCode, ViewObject> detectedErrors) {
        super("Errors detected in time record. " +
                "empId:" + timeRecord.getEmployeeId() + " dates:" + timeRecord.getDateRange());
        this.timeRecord = timeRecord;
        this.detectedErrors = detectedErrors;
    }

    public TimeRecord getTimeRecord() {
        return timeRecord;
    }

    public Map<TimeRecordErrorCode, ViewObject> getDetectedErrors() {
        return detectedErrors;
    }
}
