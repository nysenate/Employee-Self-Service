package gov.nysenate.ess.web.security.realm;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken.REDMINE_PRINCIPAL;

/**
 * Authenticates Redmine integration requests using a simple API key.
 * 
 * This realm validates API keys for Redmine system integration and provides
 * minimal permissions required to access Redmine-specific endpoints.
 * URL restrictions are handled by the filter chain configuration in shiro.ini.
 */
@Component
public class EssRedmineKeyRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(EssRedmineKeyRealm.class);

    @Value("${auth.redmine.enabled:false}")
    private boolean redmineAuthEnabled;

    @Value("${auth.redmine.api.key:}")
    private String redmineApiKey;

    @Override
    public Class getAuthenticationTokenClass() {
        return RedmineKeyAuthenticationToken.class;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token == null) {
            throw new IllegalArgumentException("Token parameter cannot be null.");
        }

        if (!redmineAuthEnabled) {
            throw new DisabledAccountException("Redmine authentication is disabled.");
        }

        if (!StringUtils.hasText(redmineApiKey)) {
            throw new AuthenticationException("Redmine API key is not configured.");
        }

        RedmineKeyAuthenticationToken redmineToken = (RedmineKeyAuthenticationToken) token;
        String providedKey = redmineToken.apiKey();

        if (StringUtils.hasText(providedKey) && providedKey.equals(redmineApiKey)) {
            logger.info("Redmine authentication successful");
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        } else {
            logger.warn("Redmine authentication failed - invalid API key provided");
            throw new IncorrectCredentialsException("Invalid Redmine API key.");
        }
    }

    /**
     * Provides minimal authorization for Redmine integration.
     * Grants basic employee permission needed for API access.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        try {
            String principal = (String) principals.getPrimaryPrincipal();
            if (REDMINE_PRINCIPAL.equals(principal)) {
                // Grant minimal permissions needed for Redmine endpoints
                ImmutableList<Permission> permissions = ImmutableList.of(
                    SimpleEssPermission.REDMINE_API_ACCESS.getPermission()
                );
                authInfo.addObjectPermissions(permissions);
                logger.debug("Granted Redmine integration permissions");
            }
        } catch (ClassCastException castEx) {
            logger.debug("Redmine realm could not retrieve principal for authorization.");
        } catch (Exception ex) {
            logger.error("An error occurred during Redmine Authorization.", ex);
        }
        return authInfo;
    }
}