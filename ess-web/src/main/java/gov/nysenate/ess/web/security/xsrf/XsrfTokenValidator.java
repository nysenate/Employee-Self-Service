package gov.nysenate.ess.web.security.xsrf;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class is used to set and validate XSRF tokens. An XSRF (CSRF) token is simply a randomly
 * generated hash that is stored in both the session and as a hidden field in the form. When the user
 * submits a POST request, the token from the form is validated against the token in the session. If they
 * are the same, it means that request is coming from the form rendered by the server and is not some
 * spoofed request url crafted by an attacker.
 */
public class XsrfTokenValidator implements XsrfValidator
{
    private static final Logger logger = LoggerFactory.getLogger(XsrfTokenValidator.class);

    /** The number of random bytes to be generated */
    protected static int BYTES_SIZE = 32;

    /** The random number generator instance */
    protected SecureRandomNumberGenerator secRandGen;

    public XsrfTokenValidator() {
        this(BYTES_SIZE);
    }

    public XsrfTokenValidator(int bytesSize) {
        this.secRandGen = new SecureRandomNumberGenerator();
        this.secRandGen.setDefaultNextBytesSize(bytesSize);
    }

    /**
     * Generates a random Base64 encoded string that can be used as an XSRF token.
     * @return base64 encoded String
     */
    public String generateXsrfToken() {
        return secRandGen.nextBytes().toBase64();
    }

    /**
     * Retrieves xsrf token stored in session or generates one if it doesn't exist and saves it to the
     * request (and session if not set) as attributes. If the method succeeds the xsrf token will be returned.
     * Otherwise a null value will be returned.
     *
     * @param request HttpServletRequest
     * @param session HttpSession
     * @return String if success, or null otherwise.
     */
    public String saveXsrfToken(HttpServletRequest request, HttpSession session) {
        String xsrfTokenFromSession = getXsrfTokenFromSession(session);
        String xsrfToken = (xsrfTokenFromSession != null) ? xsrfTokenFromSession : generateXsrfToken();
        if (setXsrfTokenInSession(session, xsrfToken) &&
            setXsrfTokenInRequestAttribute(request, xsrfToken)) {
            return xsrfToken;
        }
        return null;
    }

    /**
     * Validates an {xsrfToken} string against the token stored in the session.
     * Returns an XsrfTokenStatus enum value.
     *
     * @param session HttpSession
     * @param xsrfToken String
     * @return XsrfTokenStatus
     */
    public XsrfTokenStatus validateXsrfToken(HttpSession session, String xsrfToken) {
        if (xsrfToken != null && !xsrfToken.isEmpty()) {
            if (session != null) {
                String xsrfTokenSession = getXsrfTokenFromSession(session);
                if (xsrfTokenSession != null && !xsrfTokenSession.isEmpty()) {
                    if (xsrfToken.equals(xsrfTokenSession)) {
                        return XsrfTokenStatus.VALIDATED;
                    }
                    return XsrfTokenStatus.INVALID_XSRF_TOKEN;
                }
                return XsrfTokenStatus.EMPTY_XSRF_SESSION_TOKEN;
            }
            return XsrfTokenStatus.EMPTY_SESSION;
        }
        return XsrfTokenStatus.EMPTY_XSRF_TOKEN;
    }

    /**
     * Sets the XSRF token as a session attribute.
     * @param session HttpSession
     * @param xsrfToken String
     * @return true if success, false otherwise
     */
    protected boolean setXsrfTokenInSession(HttpSession session, String xsrfToken) {
        if (session != null) {
            session.setAttribute(XSRF_TOKEN_SESSION_KEY, xsrfToken);
            return true;
        }
        logger.warn("Failed to set XSRF Token in session due to a null session.");
        return false;
    }

    /**
     * Returns the XSRF token stored in the session.
     * @param session HttpSession
     * @return String
     */
    protected String getXsrfTokenFromSession(HttpSession session) {
        if (session != null && session.getAttribute(XSRF_TOKEN_SESSION_KEY) != null) {
            return session.getAttribute(XSRF_TOKEN_SESSION_KEY).toString();
        }
        return null;
    }

    /**
     * Sets the XSRF token as a request attribute.
     * @param request HttpServletRequest
     * @param xsrfToken String
     * @return true if success, false otherwise
     */
    protected boolean setXsrfTokenInRequestAttribute(HttpServletRequest request, String xsrfToken) {
        if (request != null) {
            request.setAttribute(XSRF_TOKEN_REQUEST_ATTR_KEY, xsrfToken);
            return true;
        }
        logger.warn("Failed to set XSRF Token in request attribute due to null request.");
        return false;
    }
}
