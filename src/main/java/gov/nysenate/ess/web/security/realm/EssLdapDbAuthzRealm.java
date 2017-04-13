package gov.nysenate.ess.web.security.realm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.auth.LdapAuthResult;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.security.authentication.LdapAuthService;
import gov.nysenate.ess.core.service.security.authorization.EssPermissionService;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private RoleDao roleDao;
    @Autowired private EssPermissionService essPermissionService;

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

        SenatePerson user;
        int empId;
        try {
            user = (SenatePerson) principals.getPrimaryPrincipal();
            empId = user.getEmployeeId();
        } catch (Exception ex) {
            // If there is an exception, log it and return empty auth info.
            // todo send error notification
            logger.error("Could not retrieve principal for authorization", ex);
            return authInfo;
        }

        try {
            // Get employee record
            Employee employee = employeeInfoService.getEmployee(empId);

            // Get and set employee roles
            ImmutableSet<EssRole> roles = roleDao.getRoles(employee);
            List<String> roleStrings = roles.stream().map(EssRole::name).collect(Collectors.toList());
            authInfo.addRoles(roleStrings);

            // Get and set employee permissions
            ImmutableList<Permission> permissions = essPermissionService.getPermissions(employee, roles);
            authInfo.addObjectPermissions(permissions);
        } catch (Exception ex) {
            // If there is an exception, log it and return empty auth info.
            // todo send error notification
            logger.error("Error while retrieving authorization info for employee: " + empId, ex);
        }

        return authInfo;
    }
}
