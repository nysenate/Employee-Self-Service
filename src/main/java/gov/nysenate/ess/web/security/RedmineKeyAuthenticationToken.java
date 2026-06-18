package gov.nysenate.ess.web.security;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * A custom {@code AuthenticationToken} which uses the Redmine API key
 * for authentication to Redmine-specific endpoints.
 * This allows Shiro to authenticate users accessing Redmine integration endpoints
 * using a simple API key mechanism.
 */
public record RedmineKeyAuthenticationToken(String apiKey) implements AuthenticationToken {

    public static final String REDMINE_PRINCIPAL  = "redmine-integration";

    @Override
    public Object getPrincipal() {
        return REDMINE_PRINCIPAL;
    }

    @Override
    public Object getCredentials() {
        return apiKey();
    }

    @Override
    public String toString() {
        return "RedmineKeyAuthenticationToken{principal=" + getPrincipal() + "}";
    }
}