package gov.nysenate.ess.web.security.xsrf;

/**
 * Indicates the status of a validation attempt on a XSRF Token.
 */
public enum XsrfTokenStatus
{
    VALIDATED(true),
    EMPTY_XSRF_TOKEN(false),
    EMPTY_SESSION(false),
    EMPTY_XSRF_SESSION_TOKEN(false),
    INVALID_XSRF_TOKEN(false);

    private final boolean success;

    XsrfTokenStatus(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
