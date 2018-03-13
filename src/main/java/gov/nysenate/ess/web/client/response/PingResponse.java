package gov.nysenate.ess.web.client.response;

import gov.nysenate.ess.core.client.response.base.SimpleResponse;

public class PingResponse extends SimpleResponse {

    private long remainingInactivity;

    public PingResponse(long remainingInactivity) {
        super(true, "ping!", "ping");
        this.remainingInactivity = remainingInactivity;
    }

    public long getRemainingInactivity() {
        return remainingInactivity;
    }
}
