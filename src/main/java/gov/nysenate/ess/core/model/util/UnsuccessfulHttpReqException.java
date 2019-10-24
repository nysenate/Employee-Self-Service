package gov.nysenate.ess.core.model.util;

import gov.nysenate.ess.core.util.HttpUtils;

/**
 * Thrown by {@link HttpUtils} when an http request
 * returns a non successful response (status code != 2xx)
 */
public class UnsuccessfulHttpReqException extends RuntimeException {

    public UnsuccessfulHttpReqException(String message) {
        super(message);
    }

    public UnsuccessfulHttpReqException(Throwable cause) {
        super(cause);
    }

    public UnsuccessfulHttpReqException(String message, Throwable cause) {
        super(message, cause);
    }
}
