package gov.nysenate.ess.time.client.response;

import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.time.client.view.attendance.TimeRecordErrorView;
import gov.nysenate.ess.time.client.view.attendance.TimeRecordView;
import gov.nysenate.ess.time.service.attendance.validation.TimeRecordErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ErrorResponse;

import java.util.Map;
import java.util.stream.Collectors;

public class InvalidTimeRecordResponse extends ErrorResponse
{

    protected MapView<Integer, TimeRecordErrorView> errorData;
    protected TimeRecordView timeRecord;

    public InvalidTimeRecordResponse(TimeRecordView timeRecord, Map<TimeRecordErrorCode, ViewObject> errorData) {
        super(ErrorCode.INVALID_TIME_RECORD);
        this.timeRecord = timeRecord;
        this.errorData = MapView.of(
                errorData.entrySet().stream().collect(Collectors.toMap(
                        entry -> entry.getKey().getCode(),
                        entry -> new TimeRecordErrorView(entry.getKey(), entry.getValue()))) );
        this.responseType = "invalid time record";
    }

    public MapView<Integer, TimeRecordErrorView> getErrorData() {
        return errorData;
    }

    public TimeRecordView getTimeRecord() {
        return timeRecord;
    }
}
