package gov.nysenate.ess.core.service.security.authentication;

import gov.nysenate.ess.core.model.auth.LdapAuthResult;

public interface LdapAuthService
{
    /**
     * Authenticates an LDAP user via their uid and password. The method will always return an
     * LdapAuthResult containing a status and a resolved Name if successful.
     *
     * @param uid String username
     * @param credentials String password
     * @return LdapAuthResult
     */
    LdapAuthResult authenticateUserByUid(String uid, String credentials);

    /**
     * Allows a user to authenticate against the LDAP server OR against the supplied 'masterPass'. This should
     * only be used for dev/testing deployments where a user may want to login as someone other than him/herself.
     *
     * @param uid String - username
     * @param suppliedPass String - given password
     * @param masterPass String - password to check against
     * @return LdapAuthResult
     */
    LdapAuthResult authenticateUserByUidWithoutCreds(String uid, String suppliedPass, String masterPass);
}
