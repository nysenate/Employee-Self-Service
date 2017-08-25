package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.client.response.auth.AuthorizationResponse;
import gov.nysenate.ess.core.util.HttpResponseUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.security.IpAuthenticationToken;
import gov.nysenate.ess.web.security.realm.EssIpAuthzRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static gov.nysenate.ess.core.model.auth.AuthorizationStatus.UNAUTHENTICATED;

/**
 * Checks if API requests are authenticated.
 * If subject is not authenticated try to login with a {@link IpAuthenticationToken} via the {@link EssIpAuthzRealm}.
 * If that login fails, write a response informing user they are unauthenticated.
 */
public class EssApiAuthenticationFilter extends AuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken authToken = new IpAuthenticationToken(request.getRemoteAddr());
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(authToken);
            return true;
        }
        catch(AuthenticationException ex) {
            writeApiUnauthenticatedResponse(request, response);
        }
        return false;
    }

    /**
     * Generate an unauthenticated error response for an unauthenticated api call
     * @param request {@link ServletRequest}
     * @param response {@link ServletResponse}
     * @throws IOException
     */
    private void writeApiUnauthenticatedResponse(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        MediaType mediaType;
        // Send Json unless the content type is explicitly set
        try {
            mediaType = MediaType.parseMediaType(request.getContentType());
        } catch (InvalidMediaTypeException ex) {
            mediaType = MediaType.APPLICATION_JSON;
        }

        AuthorizationResponse authResponse = new AuthorizationResponse(
                UNAUTHENTICATED,
                SecurityUtils.getSubject(),
                HttpResponseUtils.getFullUrl((HttpServletRequest) request));

        HttpResponseUtils.preventCaching(httpResponse);
        // Write authorization response in desired format
        if (mediaType == MediaType.APPLICATION_JSON) {
            httpResponse.getWriter().append(OutputUtils.toJson(authResponse));
        } else if (mediaType == MediaType.APPLICATION_XML) {
            httpResponse.getWriter().append(OutputUtils.toXml(authResponse));
        } else {
            // Just send the error code if the format isn't supported
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        httpResponse.setContentType(mediaType.getType());
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.flushBuffer();
    }
}
