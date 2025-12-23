package gov.nysenate.ess.web.security.realm;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken;
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

import static gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken.BACHELP_PRINCIPAL;

/**
 * Authenticates BACHelp integration requests using a simple API key.
 * 
 * This realm validates API keys for BACHelp system integration and provides
 * minimal permissions required to access BACHelp-specific endpoints.
 * URL restrictions are handled by the filter chain configuration in shiro.ini.
 */
@Component
public class EssBACHelpKeyRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(EssBACHelpKeyRealm.class);

    @Value("${auth.bachelp.enabled:false}")
    private boolean bachelpAuthEnabled;

    @Value("${auth.bachelp.api.key:}")
    private String bachelpApiKey;

    @Override
    public Class getAuthenticationTokenClass() {
        return BACHelpKeyAuthenticationToken.class;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (token == null) {
            throw new IllegalArgumentException("Token parameter cannot be null.");
        }

        if (!bachelpAuthEnabled) {
            throw new DisabledAccountException("BACHelp authentication is disabled.");
        }

        if (!StringUtils.hasText(bachelpApiKey)) {
            throw new AuthenticationException("BACHelp API key is not configured.");
        }

        BACHelpKeyAuthenticationToken bachelpToken = (BACHelpKeyAuthenticationToken) token;
        String providedKey = bachelpToken.apiKey();

        if (StringUtils.hasText(providedKey) && providedKey.equals(bachelpApiKey)) {
            logger.info("BACHelp authentication successful");
            return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        } else {
            logger.warn("BACHelp authentication failed - invalid API key provided");
            throw new IncorrectCredentialsException("Invalid BACHelp API key.");
        }
    }

    /**
     * Provides minimal authorization for BACHelp integration.
     * Grants basic employee permission needed for API access.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
        try {
            String principal = (String) principals.getPrimaryPrincipal();
            if (BACHELP_PRINCIPAL.equals(principal)) {
                // Grant minimal permissions needed for BACHelp endpoints
                ImmutableList<Permission> permissions = ImmutableList.of(
                    SimpleEssPermission.BACHELP_API_ACCESS.getPermission()
                );
                authInfo.addObjectPermissions(permissions);
                logger.debug("Granted BACHelp integration permissions");
            }
        } catch (ClassCastException castEx) {
            logger.debug("BACHelp realm could not retrieve principal for authorization.");
        } catch (Exception ex) {
            logger.error("An error occurred during BACHelp Authorization.", ex);
        }
        return authInfo;
    }
}