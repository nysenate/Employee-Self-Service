package gov.nysenate.ess.web.security;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * A custom {@code AuthenticationToken} which uses the BACHelp API key
 * for authentication to BACHelp-specific endpoints.
 * This allows Shiro to authenticate users accessing BACHelp integration endpoints
 * using a simple API key mechanism.
 */
public record BACHelpKeyAuthenticationToken(String apiKey) implements AuthenticationToken {

    public static final String BACHELP_PRINCIPAL  = "bachelp-integration";

    @Override
    public Object getPrincipal() {
        return BACHELP_PRINCIPAL;
    }

    @Override
    public Object getCredentials() {
        return apiKey();
    }

    @Override
    public String toString() {
        return "BACHelpKeyAuthenticationToken{principal=" + getPrincipal() + "}";
    }
}