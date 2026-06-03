package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.client.response.auth.AuthenticationResponse;
import gov.nysenate.ess.core.model.auth.AuthenticationStatus;
import gov.nysenate.ess.core.util.HttpResponseUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static gov.nysenate.ess.core.model.auth.AuthenticationStatus.REDMINE_MISSING_API_KEY;
import static gov.nysenate.ess.core.model.auth.AuthenticationStatus.REDMINE_INVALID_API_KEY;
import static gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken.REDMINE_PRINCIPAL;

/**
 * Authentication filter for Redmine integration API endpoints.
 * 
 * Extracts API key from X-API-Key header and attempts authentication
 * using the Redmine key realm. The actual key validation is performed by the realm.
 */
public class EssRedmineAuthenticationFilter extends AuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(EssRedmineAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (!StringUtils.hasText(apiKey)) {
            logger.debug("Redmine API key header missing or empty");
            writeRedmineAuthenticationResponse(request, response, REDMINE_MISSING_API_KEY);
            return false;
        }

        // Create authentication token and let the realm validate the key
        AuthenticationToken authToken = new RedmineKeyAuthenticationToken(apiKey);
        Subject subject = SecurityUtils.getSubject();
        
        try {
            subject.login(authToken);
            logger.debug("Redmine authentication successful");
            return true;
        } catch (AuthenticationException ex) {
            logger.debug("Redmine authentication failed: {}", ex.getMessage());
            writeRedmineAuthenticationResponse(request, response, REDMINE_INVALID_API_KEY);
            return false;
        }
    }

    /**
     * Generate an authentication error response for a failed Redmine API authentication
     */
    private void writeRedmineAuthenticationResponse(ServletRequest request, ServletResponse response, AuthenticationStatus status) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        MediaType mediaType;
        
        // Send JSON unless the content type is explicitly set
        try {
            mediaType = MediaType.parseMediaType(request.getContentType());
        } catch (InvalidMediaTypeException ex) {
            mediaType = MediaType.APPLICATION_JSON;
        }

        AuthenticationResponse authResponse = new AuthenticationResponse(
                status,
                REDMINE_PRINCIPAL,
                0,
                null);

        HttpResponseUtils.preventCaching(httpResponse);
        
        // Write authorization response in desired format
        if (mediaType.equals(MediaType.APPLICATION_JSON)) {
            httpResponse.getWriter().append(OutputUtils.toJson(authResponse));
        } else if (mediaType.equals(MediaType.APPLICATION_XML)) {
            httpResponse.getWriter().append(OutputUtils.toXml(authResponse));
        } else {
            // Just send the error code if the format isn't supported
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, status.getStatusMessage());
            return;
        }
        
        httpResponse.setContentType(mediaType.toString());
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.flushBuffer();
    }
}