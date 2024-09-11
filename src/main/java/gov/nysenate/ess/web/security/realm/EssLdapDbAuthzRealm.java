package gov.nysenate.ess.web.security.realm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.auth.LdapAuthResult;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.security.authentication.LdapAuthService;
import gov.nysenate.ess.core.service.security.authorization.permission.EssPermissionService;
import gov.nysenate.ess.core.service.security.authorization.role.EssRoleService;
import gov.nysenate.ess.web.security.exception.LdapMismatchException;
import gov.nysenate.ess.web.security.exception.NameNotFoundException;
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

    private LdapAuthService essLdapAuthService;
    private EmployeeInfoService employeeInfoService;
    private EmployeeDao employeeDao;
    private EssRoleService essRoleService;
    private EssPermissionService essPermissionService;
    private SlackChatService slackChatService;

    @Autowired
    public EssLdapDbAuthzRealm(LdapAuthService essLdapAuthService, EmployeeInfoService employeeInfoService,
                               EssRoleService essRoleService, EssPermissionService essPermissionService,
                               SlackChatService slackChatService, EmployeeDao employeeDao) {
        this.essLdapAuthService = essLdapAuthService;
        this.employeeInfoService = employeeInfoService;
        this.essRoleService = essRoleService;
        this.essPermissionService = essPermissionService;
        this.slackChatService = slackChatService;
        this.employeeDao = employeeDao;
    }

    @Override
    public Class getAuthenticationTokenClass() {
        return UsernamePasswordToken.class;
    }

    /**
     * {@inheritDoc}
     *
     * Performs LDAP authentication using the supplied authentication token.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token instanceof UsernamePasswordToken userPassToken) {
            String username = userPassToken.getUsername();
            String password = new String(userPassToken.getPassword());
            logger.info("Authenticating user {} through the Senate LDAP.{}",
                    username, authEnabled ? "" : " (Master Pass Enabled)" );

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
                throw new UnknownAccountException("The username supplied was empty.");
            case NAME_NOT_FOUND_EXCEPTION:
                throw new NameNotFoundException("No account was found with the supplied username");
            case MULTIPLE_MATCH_EXCEPTION:
                throw new AccountException("Multiple entries were found for the supplied username.");
            case EMPTY_CREDENTIALS:
                throw new IncorrectCredentialsException("The password supplied was empty.");
            case INCORRECT_CREDENTIALS:
                throw new IncorrectCredentialsException("The username or password is invalid.");
            case CONNECTION_ERROR:
                throw new AuthenticationException("Failed to connect to the authentication server.");
            case LDAP_MISMATCH_EXCEPTION:
                throw new LdapMismatchException("There was a mismatch between LDAP & SFMS");
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
        }
        catch(ClassCastException castEx) {
            logger.debug("Ess LDAP realm could not retrieve principal for authorization. " +
                    "This is expected when accessing the api via a whitelisted ip without logging in.");
            return authInfo;
        }
        catch (Exception ex) {
            logger.error("An error occured during LDAP Authorization.");
            return authInfo;
        }

        try {
            // Get employee record
            Employee employee = employeeInfoService.getEmployee(empId);

            // Get and set employee roles
            ImmutableSet<Enum<?>> roles = essRoleService.getRoles(employee).collect(ImmutableSet.toImmutableSet());
            List<String> roleStrings = roles.stream()
                    .map(Enum::name)
                    .collect(Collectors.toList());
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
