package gov.nysenate.ess.core.model.auth;

/**
 * Represents the conditions that can result after an LDAP authentication attempt.
 */
public enum LdapAuthStatus
{
    AUTHENTICATED(true, "The LDAP user has been authenticated successfully."),
    INCORRECT_CREDENTIALS(false, "The LDAP user could not be authenticated due to incorrect credentials."),
    MULTIPLE_MATCH_EXCEPTION(false, "There were multiple matches for the given LDAP user."),
    NAME_NOT_FOUND_EXCEPTION(false, "There were no matches for the given LDAP user."),
    EMPTY_USERNAME(false, "The uid was empty."),
    EMPTY_CREDENTIALS(false, "The credentials were empty."),
    CONNECTION_ERROR(false, "Failed to connect with the authentication server."),
    UNKNOWN_EXCEPTION(false, "An exception occurred while attempting to authenticate LDAP user.");

    private final boolean authenticated;
    private final String message;

    LdapAuthStatus(boolean authenticated, String message) {
        this.authenticated = authenticated;
        this.message = message;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getMessage() {
        return message;
    }
}
