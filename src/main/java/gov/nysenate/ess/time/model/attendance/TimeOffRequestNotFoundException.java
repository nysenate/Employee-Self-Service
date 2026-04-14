package gov.nysenate.ess.time.model.attendance;

import java.io.Serializable;

public class TimeOffRequestNotFoundException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -5666406693010031893L;

    private int requestId;

    public TimeOffRequestNotFoundException(int requestId) {
        super("No time off request could be retrieved with a request id of " + requestId);
        this.requestId = requestId;
    }

    public int getRequestId() { return requestId; }

}
