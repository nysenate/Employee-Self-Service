package gov.nysenate.ess.web.security.realm;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.service.security.authorization.permission.AdminPermissionFactory;
import gov.nysenate.ess.web.security.IpAuthenticationToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Authenticates ESS subjects by IP Address.
 * Subjects with an ip address matching {@code ipWhitelist}
 * are authenticated without needing to log in.
 *
 * This works with {@link gov.nysenate.ess.web.security.filter.EssApiAuthenticationFilter}
 * to allow scripts from trusted networks to access the ESS API.
 *
 * Subjects authenticated in this manner are given admin permissions.
 */
@Component
public class EssIpAuthzRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(EssIpAuthzRealm.class);

    @Value("${auth.api.ip.whitelist:^$}") private String ipWhitelist;
    private static final EssRole ROLE = EssRole.ADMIN;
    @Autowired private AdminPermissionFactory adminPermissionFactory;

    @Override
    public Class getAuthenticationTokenClass() {
        return IpAuthenticationToken.class;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token == null) {
            throw new IllegalArgumentException("Token parameter cannot be null.");
        }
        IpAuthenticationToken ipToken = (IpAuthenticationToken) token;
        if (isIpWhitelisted(ipToken.getIpAddress())) {
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        }
        else {
            throw new UnknownAccountException("Client is not allowed access.");
        }
    }

    /**
     * This Realm implementation gives the admin role and associated permissions to subjects authenticated via ip address.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        try {
            String ipAddress = (String) principals.getPrimaryPrincipal();
            if (isIpWhitelisted(ipAddress)) {
                authInfo.addRole(ROLE.name());
                authInfo.addObjectPermissions(adminPermissionFactory.getPermissions(null, ImmutableSet.of(ROLE)));
            }
        }
        catch(ClassCastException castEx) {
            logger.debug("Ess IP realm could not retrieve principal for authorization. " +
                    "This is expected when logging in through the UI.");
        }
        catch (Exception ex) {
            logger.error("An error occurred during Ip Authorization.");
        }
        return authInfo;
    }

    private boolean isIpWhitelisted(String ipAddress) {
        return ipAddress.matches(ipWhitelist);
    }
}
