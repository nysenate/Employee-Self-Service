package gov.nysenate.ess.core.service.pec.external.everfi;

import java.io.IOException;

public class EverfiApiException extends IOException {

    private final int statusCode;
    private final String responseBody;

    public EverfiApiException(int statusCode, String responseBody) {
        super("Everfi API request failed with status " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
