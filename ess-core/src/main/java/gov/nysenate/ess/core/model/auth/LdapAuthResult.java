package gov.nysenate.ess.core.model.auth;

import javax.naming.Name;

/**
 * This class is used to represent the outcome of an LDAP authentication request. It
 * stores the status as well as the validated Name if authentication succeeded.
 */
public class LdapAuthResult
{
    private final LdapAuthStatus authStatus;
    private final String uid;
    private final Name name;
    private final SenateLdapPerson person;

    public LdapAuthResult(LdapAuthStatus status, String uid) {
        this(status, uid, null, null);
    }

    public LdapAuthResult(LdapAuthStatus status, String uid, Name name, SenateLdapPerson person) {
        this.authStatus = status;
        this.uid = uid;
        this.name = name;
        this.person = person;
    }

    public boolean isAuthenticated() {
        return (this.authStatus != null && this.authStatus.equals(LdapAuthStatus.AUTHENTICATED));
    }

    public LdapAuthStatus getAuthStatus() {
        return authStatus;
    }

    public String getUid() {
        return uid;
    }

    public Name getName() {
        return name;
    }

    public SenateLdapPerson getPerson() {
        return person;
    }
}