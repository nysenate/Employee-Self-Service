package gov.nysenate.ess.web.security;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * A custom {@code AuthenticationToken} which uses the client's IP address
 * as their principal and credentials.
 *
 * This allows Shiro to authenticate, authorize and cache a user by their IP address.
 */
public class IpAuthenticationToken implements AuthenticationToken {

    private String ipAddress;

    public IpAuthenticationToken(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public Object getPrincipal() {
        return getIpAddress();
    }

    @Override
    public Object getCredentials() {
        return getIpAddress();
    }

    public String getIpAddress() {
        return this.ipAddress;
    }
}
