package gov.nysenate.ess.web.security.filter;

import gov.nysenate.ess.core.annotation.UnitTest;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class LoginRedirectRequestTest {

    @Test
    public void savesOnlyPageNavigations() {
        MockHttpServletRequest navigation = new MockHttpServletRequest("GET", "/time");
        navigation.addHeader("Sec-Fetch-Mode", "navigate");
        navigation.addHeader("Sec-Fetch-Dest", "document");
        assertTrue(LoginRedirectRequest.isPageNavigation(navigation));

        MockHttpServletRequest legacyNavigation = new MockHttpServletRequest("GET", "/time");
        legacyNavigation.addHeader("Accept", "text/html,application/xhtml+xml");
        assertTrue(LoginRedirectRequest.isPageNavigation(legacyNavigation));

        MockHttpServletRequest devtools = new MockHttpServletRequest("GET", "/.well-known/appspecific/com.chrome.devtools.json");
        devtools.addHeader("Sec-Fetch-Mode", "no-cors");
        devtools.addHeader("Sec-Fetch-Dest", "empty");
        devtools.addHeader("Accept", "*/*");
        assertFalse(LoginRedirectRequest.isPageNavigation(devtools));

        MockHttpServletRequest favicon = new MockHttpServletRequest("GET", "/favicon.ico");
        favicon.addHeader("Accept", "image/avif,image/webp,*/*");
        assertFalse(LoginRedirectRequest.isPageNavigation(favicon));

        MockHttpServletRequest xhr = new MockHttpServletRequest("GET", "/time");
        xhr.addHeader("Accept", "text/html");
        xhr.addHeader("X-Requested-With", "XMLHttpRequest");
        assertFalse(LoginRedirectRequest.isPageNavigation(xhr));

        MockHttpServletRequest post = new MockHttpServletRequest("POST", "/time");
        post.addHeader("Sec-Fetch-Mode", "navigate");
        post.addHeader("Sec-Fetch-Dest", "document");
        assertFalse(LoginRedirectRequest.isPageNavigation(post));
    }

    @Test
    public void backgroundRequestDoesNotReplaceSavedPage() throws Exception {
        RecordingAuthenticationFilter filter = new RecordingAuthenticationFilter();

        MockHttpServletRequest page = new MockHttpServletRequest("GET", "/time");
        page.addHeader("Sec-Fetch-Mode", "navigate");
        page.addHeader("Sec-Fetch-Dest", "document");
        filter.onAccessDenied(page, new MockHttpServletResponse());

        MockHttpServletRequest background = new MockHttpServletRequest("GET", "/.well-known/appspecific/com.chrome.devtools.json");
        background.addHeader("Sec-Fetch-Mode", "no-cors");
        background.addHeader("Sec-Fetch-Dest", "empty");
        filter.onAccessDenied(background, new MockHttpServletResponse());

        assertEquals("/time", filter.savedUrl);
        assertEquals(2, filter.loginRedirects);
    }

    private static class RecordingAuthenticationFilter extends EssAuthenticationFilter {
        private String savedUrl;
        private int loginRedirects;

        private RecordingAuthenticationFilter() {
            super(null, null);
        }

        @Override
        protected void saveRequest(ServletRequest request) {
            savedUrl = ((MockHttpServletRequest) request).getRequestURI();
        }

        @Override
        protected void redirectToLogin(ServletRequest request, ServletResponse response) {
            loginRedirects++;
        }
    }
}
