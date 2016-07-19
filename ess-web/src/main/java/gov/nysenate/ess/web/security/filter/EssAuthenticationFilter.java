package gov.nysenate.ess.web.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.client.response.auth.AuthenticationResponse;
import gov.nysenate.ess.core.client.response.auth.AuthorizationResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.AuthenticationStatus;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.util.HttpResponseUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.web.security.xsrf.XsrfValidator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static gov.nysenate.ess.core.model.auth.AuthorizationStatus.UNAUTHENTICATED;

/**
 *
 */
public class EssAuthenticationFilter extends AuthenticationFilter
{
    private static final Logger logger = LoggerFactory.getLogger(EssAuthenticationFilter.class);

    public static final String DEFAULT_USERNAME_PARAM = "username";
    public static final String DEFAULT_PASSWORD_PARAM = "password";
    public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";

    private String usernameParam = DEFAULT_USERNAME_PARAM;
    private String passwordParam = DEFAULT_PASSWORD_PARAM;
    private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;

    @Resource(name = "xsrfValidator", description = "Generates/Validates XSRF Tokens")
    private XsrfValidator xsrfValidator;

    /** --- Overrides --- */

    /**
     * Overrides functionality so that login page requests are redirected back to the previous url if
     * the user is already authenticated.
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)) {
            if (isLoginSubmission(request, response)) {
                return executeLogin(request, response);
            }
            else {
                if (isAuthenticated(request, response)) {
                    /** User is already authenticated and should be sent back to where they were before. */
                    issueSuccessRedirect(request, response);
                    return false;
                }
                else {
                    /** User is not authenticated and should be able to view the login page. */
                    return true;
                }
            }
        }
        /** If the unauthenticated request was an API request,
         * send an appropriately formatted response instead of redirecting to login */
        if (((HttpServletRequest) request).getRequestURI().startsWith(BaseRestApiCtrl.REST_PATH)) {
            writeApiUnauthenticatedResponse(request, response);
            return false;
        }
        /** User should be redirected to the login page since they do not have access. */
        saveRequestAndRedirectToLogin(request, response);
        return false;
    }

    /**
     * Overrides functionality so that requests to the login page are denied once the user is
     * already authenticated.
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean authenticated = isAuthenticated(request, response);
        boolean loginRequest = isLoginRequest(request, response);
        return authenticated && !loginRequest;
    }

    /** --- Internals --- */

    protected String applyXsrfToken(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        return xsrfValidator.saveXsrfToken((HttpServletRequest) request, (HttpSession) subject.getSession());
    }

    /**
     *
     * @param request
     * @param response
     * @return
     */
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String username = getUsernameFromRequest(request);
        String password = getPasswordFromRequest(request);
        boolean rememberMe = getRememberMeFromRequest(request);
        String host = getHost(request);

        AuthenticationToken authToken = createAuthToken(username, password, rememberMe, host);
        Subject subject = getSubject(request, response);

        AuthenticationStatus authStatus;

        try {
            subject.login(authToken);
            authStatus = AuthenticationStatus.AUTHENTICATED;
            return onLoginSuccess(authToken, authStatus, subject, request, response);
        }
        catch(ExpiredCredentialsException ex) {
            authStatus = AuthenticationStatus.EXPIRED_CREDENTIALS;
        }
        catch(CredentialsException ex) {
            authStatus = AuthenticationStatus.INCORRECT_CREDENTIALS;
        }
        catch(UnknownAccountException ex) {
            authStatus = AuthenticationStatus.UNKNOWN_ACCOUNT;
        }
        catch(ExcessiveAttemptsException ex) {
            authStatus = AuthenticationStatus.EXCESSIVE_ATTEMPTS;
        }
        catch(DisabledAccountException ex) {
            authStatus = AuthenticationStatus.DISABLED_ACCOUNT;
        }
        catch(AuthenticationException ex) {
            logger.debug("Authentication exception!", ex);
            authStatus = AuthenticationStatus.FAILURE;
        }
        return onLoginFailure(authToken, authStatus, subject, request, response);
    }

    /**
     *
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     */
    protected boolean onLoginSuccess(AuthenticationToken token, AuthenticationStatus authStatus, Subject subject,
                                     ServletRequest request, ServletResponse response) throws IOException {

        SenateLdapPerson user = (SenateLdapPerson) subject.getPrincipal();
        String redirectUrl = getSuccessRedirectUrl(request);

        AuthenticationResponse authResponse = new AuthenticationResponse(authStatus, user.getUid(), redirectUrl);
        HttpResponseUtils.writeHttpResponse((HttpServletRequest) request, (HttpServletResponse) response, authResponse);
        response.flushBuffer();

        logger.debug("Login for user {} was successful.", user);
        return false;
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationStatus authStatus, Subject subject,
                                     ServletRequest request, ServletResponse response) {

        String user = (String) token.getPrincipal();
        String redirectUrl = getLoginUrl();

        AuthenticationResponse authResponse = new AuthenticationResponse(authStatus, user, redirectUrl);
        HttpResponseUtils.writeHttpResponse((HttpServletRequest) request, (HttpServletResponse) response, authResponse);

        logger.debug("Login failure for user {} with status {}", user, authStatus);
        return false;
    }

    /**
     *
     * @param request
     * @return
     */
    protected String getSuccessRedirectUrl(ServletRequest request) {
        String redirectUrl = WebUtils.getContextPath((HttpServletRequest) request) + getSuccessUrl();
        SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
        if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase(GET_METHOD)) {
            redirectUrl = savedRequest.getRequestUrl();
        }
        if (redirectUrl.contains("api")) { // if the savedRequest is an API call, then we reset it to default page
            redirectUrl = getLoginUrl();
        }
        return redirectUrl;
    }

    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
        return isLoginRequest(request, response) &&
               (request instanceof HttpServletRequest) &&
               WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
    }

    protected boolean isAuthenticated(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);
        return subject.isAuthenticated();
    }

    protected AuthenticationToken createAuthToken(String username, String password, boolean rememberMe, String host) {
        return new UsernamePasswordToken(username, password, rememberMe, host);
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

    /** Functional Getters/Setters */

    protected String getUsernameFromRequest(ServletRequest request) {
        return WebUtils.getCleanParam(request, getUsernameParam());
    }

    protected String getPasswordFromRequest(ServletRequest request) {
        return WebUtils.getCleanParam(request, getPasswordParam());
    }

    protected boolean getRememberMeFromRequest(ServletRequest request) {
        return WebUtils.isTrue(request, getRememberMeParam());
    }

    protected String getHost(ServletRequest request) {
        return request.getRemoteHost();
    }

    /** Basic Getters/Setters */

    public String getUsernameParam() {
        return usernameParam;
    }

    public void setUsernameParam(String usernameParam) {
        this.usernameParam = usernameParam;
    }

    public String getPasswordParam() {
        return passwordParam;
    }

    public void setPasswordParam(String passwordParam) {
        this.passwordParam = passwordParam;
    }

    public String getRememberMeParam() {
        return rememberMeParam;
    }

    public void setRememberMeParam(String rememberMeParam) {
        this.rememberMeParam = rememberMeParam;
    }
}