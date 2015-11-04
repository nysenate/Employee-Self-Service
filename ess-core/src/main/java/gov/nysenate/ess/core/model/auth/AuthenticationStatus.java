package gov.nysenate.ess.core.model.auth;

/**
 * Represents the authentication state just after a login attempt.
 */
public enum AuthenticationStatus
{
    AUTHENTICATED(true, "Authenticated successfully."),
    INCORRECT_CREDENTIALS(false, "The username or password is invalid."),
    UNKNOWN_ACCOUNT(false, "The username is not registered."),
    EXCESSIVE_ATTEMPTS(false, "Too many failed login attempts have been detected."),
    EXPIRED_CREDENTIALS(false, "The account is expired."),
    DISABLED_ACCOUNT(false, "The account is disabled."),
    FAILURE(false, "Sorry, the username or password is not recognized."),
    ERROR(false, "An error occurred during authentication.");

    protected boolean authenticated;
    protected String statusMessage;

    AuthenticationStatus(boolean authenticated, String statusMessage) {
        this.authenticated = authenticated;
        this.statusMessage = statusMessage;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
