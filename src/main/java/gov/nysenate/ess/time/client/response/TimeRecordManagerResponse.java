package gov.nysenate.ess.time.client.response;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;

public class TimeRecordManagerResponse extends SimpleResponse {

    private int recordsSaved;

    public TimeRecordManagerResponse(int empId, int recordsSaved) {
        super(true, "Ran time record manager for " + empId, "time-record-manager-run-success");
        this.recordsSaved = recordsSaved;
    }

    public int getRecordsSaved() {
        return recordsSaved;
    }
}
