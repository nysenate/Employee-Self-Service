package gov.nysenate.ess.travel.allowance.gsa;

import gov.nysenate.ess.travel.allowance.gsa.service.GsaClient;

/**
 * Indicates an exception in the {@link GsaClient} when querying the GSA API.
 */
public class GsaClientException extends RuntimeException {

    public GsaClientException(String message) {
        super(message);
    }

    public GsaClientException(Throwable cause) {
        super(cause);
    }

    public GsaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
