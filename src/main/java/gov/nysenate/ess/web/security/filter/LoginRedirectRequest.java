package gov.nysenate.ess.web.security.filter;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/** Identifies requests whose URL should be used as the destination after login. */
final class LoginRedirectRequest {
    private LoginRedirectRequest() {}

    static boolean isPageNavigation(ServletRequest request) {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            return false;
        }
        if (!"GET".equalsIgnoreCase(httpRequest.getMethod())) {
            return false;
        }
        if ("XMLHttpRequest".equalsIgnoreCase(httpRequest.getHeader("X-Requested-With"))) {
            return false;
        }

        String fetchMode = httpRequest.getHeader("Sec-Fetch-Mode");
        String fetchDestination = httpRequest.getHeader("Sec-Fetch-Dest");
        if (fetchMode != null || fetchDestination != null) {
            return "navigate".equalsIgnoreCase(fetchMode) && "document".equalsIgnoreCase(fetchDestination);
        }

        // Older clients may omit Fetch Metadata headers. A page request should still accept HTML.
        String accept = httpRequest.getHeader("Accept");
        return accept != null && accept.toLowerCase(Locale.ROOT).contains("text/html");
    }
}
