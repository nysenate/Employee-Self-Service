package gov.nysenate.ess.web.security.realm;

import gov.nysenate.ess.core.model.auth.LdapAuthResult;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.service.auth.LdapAuthService;
import gov.nysenate.ess.seta.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.supply.security.SupplyAuthorization;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Realm implementation for providing authentication via the Senate's LDAP server and authorization via
 * personnel data stored in SFMS.
 *
 * This is similar to {@link org.apache.shiro.realm.ldap.AbstractLdapRealm AbstractLdapRealm} but since
 * we're using Spring LDAP to handle low level LDAP operations it wasn't necessary to extend that class.
 */
@Component
public class EssLdapDbAuthzRealm extends AuthorizingRealm
{
    private static final Logger logger = LoggerFactory.getLogger(EssLdapDbAuthzRealm.class);

    // If 'authEnabled' is set to false, any user can be authenticated using the master password below.
    @Value("${auth.enabled:true}") private boolean authEnabled;
    @Value("${auth.master.pass}") private String masterPass;

    @Autowired private LdapAuthService essLdapAuthService;
    @Autowired private SupervisorInfoService supervisorInfoService;
    @Autowired private SupplyAuthorization supplyAuthorization;

    /**
     * {@inheritDoc}
     *
     * Performs LDAP authentication using the supplied authentication token.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token != null && token instanceof UsernamePasswordToken) {
            UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
            String username = userPassToken.getUsername();
            String password = new String(userPassToken.getPassword());
            logger.debug("Authenticating user {} through the Senate LDAP.", username);

            LdapAuthResult authResult =
                (authEnabled) ? essLdapAuthService.authenticateUserByUid(username, password)
                              : essLdapAuthService.authenticateUserByUidWithoutCreds(username, password, masterPass);
            return queryForAuthenticationInfo(userPassToken, authResult);
        }
        throw new UnsupportedTokenException("Senate LDAP Realm only supports UsernamePasswordToken");
    }

    /**
     * If the authentication attempt was successful an AuthenticationInfo instance will be returned that simply
     * contains the username and password supplied in the token. Otherwise an AuthenticationException will be thrown.
     *
     * @param token AuthenticationToken
     * @return AuthenticationInfo
     * @throws AuthenticationException typically a CredentialsException is thrown if user/pass combo was invalid
     *                                 so catch that one first then fallback to AuthenticationException.
     */
    protected AuthenticationInfo queryForAuthenticationInfo(UsernamePasswordToken token, LdapAuthResult authResult)
            throws AuthenticationException {
        if (authResult.isAuthenticated()) {
            return new SimpleAuthenticationInfo(authResult.getPerson(), token.getPassword(), getName());
        }
        switch(authResult.getAuthStatus()) {
            case EMPTY_USERNAME:
                throw new CredentialsException("The username supplied was empty.");
            case EMPTY_CREDENTIALS:
                throw new CredentialsException("The password supplied was empty.");
            case AUTHENTICATION_EXCEPTION:
                throw new CredentialsException("The username or password is invalid.");
            case MULTIPLE_MATCH_EXCEPTION:
                throw new AccountException("Multiple entries were found for the supplied username.");
            case CONNECTION_ERROR:
                throw new AuthenticationException("Failed to connect to the authentication server.");
            default:
                throw new AuthenticationException("An unknown exception occurred while authenticating against LDAP.");
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        SenatePerson user = (SenatePerson) principals.getPrimaryPrincipal();
        if (supervisorInfoService.isSupervisorDuring(user.getEmployeeId())) {
            authInfo.addRole("supervisor");
        }
        /** Add supply permissions */
        for (String permission: supplyAuthorization.getPermissions(user)) {
            Permission p;
            WildcardPermission w;
            authInfo.addStringPermission(permission);
            authInfo.getRoles();
        }
        return authInfo;
    }

    protected String getUsername(PrincipalCollection principals) {
        return getAvailablePrincipal(principals).toString();
    }
}
