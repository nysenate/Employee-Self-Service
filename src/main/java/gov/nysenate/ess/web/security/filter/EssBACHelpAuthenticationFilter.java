package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.client.response.auth.AuthenticationResponse;
import gov.nysenate.ess.core.model.auth.AuthenticationStatus;
import gov.nysenate.ess.core.util.HttpResponseUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken;
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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static gov.nysenate.ess.core.model.auth.AuthenticationStatus.BACHELP_MISSING_API_KEY;
import static gov.nysenate.ess.core.model.auth.AuthenticationStatus.BACHELP_INVALID_API_KEY;
import static gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken.BACHELP_PRINCIPAL;

/**
 * Authentication filter for BACHelp integration API endpoints.
 * 
 * Extracts API key from X-API-Key header and attempts authentication
 * using the BACHelp key realm. The actual key validation is performed by the realm.
 */
public class EssBACHelpAuthenticationFilter extends AuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(EssBACHelpAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String apiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (!StringUtils.hasText(apiKey)) {
            logger.debug("BACHelp API key header missing or empty");
            writeBACHelpAuthenticationResponse(request, response, BACHELP_MISSING_API_KEY);
            return false;
        }

        // Create authentication token and let the realm validate the key
        AuthenticationToken authToken = new BACHelpKeyAuthenticationToken(apiKey);
        Subject subject = SecurityUtils.getSubject();
        
        try {
            subject.login(authToken);
            logger.debug("BACHelp authentication successful");
            return true;
        } catch (AuthenticationException ex) {
            logger.debug("BACHelp authentication failed: {}", ex.getMessage());
            writeBACHelpAuthenticationResponse(request, response, BACHELP_INVALID_API_KEY);
            return false;
        }
    }

    /**
     * Generate an authentication error response for a failed BACHelp API authentication
     */
    private void writeBACHelpAuthenticationResponse(ServletRequest request, ServletResponse response, AuthenticationStatus status) throws IOException {
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
                BACHELP_PRINCIPAL,
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