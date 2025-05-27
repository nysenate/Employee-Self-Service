package gov.nysenate.ess.web.security.xsrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Interface definition for implementing functionality to mitigate cross-site request forgery attacks.
 * For Reference: http://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)_Prevention_Cheat_Sheet
 */
public interface XsrfValidator
{
    /** The key used to store the xsrf token in the session */
    String XSRF_TOKEN_SESSION_KEY = "__xsrfSessionToken__";

    /** The key used to store the xsrf token as a request attribute */
    String XSRF_TOKEN_REQUEST_ATTR_KEY = "__xsrfToken__";

    String generateXsrfToken();

    String saveXsrfToken(HttpServletRequest request, HttpSession session);

    XsrfTokenStatus validateXsrfToken(HttpSession session, String xsrfToken);
}
