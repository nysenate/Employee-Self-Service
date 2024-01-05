package gov.nysenate.ess.web.security.xsrf;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.web.WebTest;
import org.apache.shiro.codec.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import java.util.Random;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class XsrfTokenValidatorIT extends WebTest
{
    private static final Logger logger = LoggerFactory.getLogger(XsrfTokenValidatorIT.class);
    private MockServletContext mockServletContext = new MockServletContext();

    private XsrfTokenValidator xsrfTokenValidator;
    private MockHttpSession mockSession;
    private MockHttpServletRequest mockRequest;

    @Before
    public void setup() {
        this.xsrfTokenValidator = new XsrfTokenValidator();
        this.mockSession = new MockHttpSession(mockServletContext);
        this.mockRequest = new MockHttpServletRequest(mockServletContext);
    }

    /** Should return the correct number of bytes when decoded. */
    @Test
    public void testGenerateXsrfTokenWithSize() {
        int bytesSize = Math.abs(new Random().nextInt()) % 1024 + 1;
        XsrfTokenValidator xsrfTokenValidator = new XsrfTokenValidator(bytesSize);
        String xsrfTokenBase64Encoded = xsrfTokenValidator.generateXsrfToken();
        byte[] xsrfTokenDecoded = Base64.decode(xsrfTokenBase64Encoded);
        assertEquals(bytesSize, xsrfTokenDecoded.length);
    }

    /** Token is saved to and retrieved from session properly. */
    @Test
    public void testGetSetXsrfTokenInSession() {
        String sessionKey = XsrfTokenValidator.XSRF_TOKEN_SESSION_KEY;
        String xsrfToken = xsrfTokenValidator.generateXsrfToken();
        assertTrue(xsrfTokenValidator.setXsrfTokenInSession(mockSession, xsrfToken));
        assertEquals(xsrfToken, xsrfTokenValidator.getXsrfTokenFromSession(mockSession));
        assertEquals(xsrfToken, mockSession.getAttribute(sessionKey));
        assertNull(xsrfTokenValidator.getXsrfTokenFromSession(null));
    }

    /** Token is saved as a request attribute properly. */
    @Test
    public void testSetXsrfTokenInRequest() {
        String requestKey = XsrfTokenValidator.XSRF_TOKEN_REQUEST_ATTR_KEY;
        String xsrfToken = xsrfTokenValidator.generateXsrfToken();
        assertTrue(xsrfTokenValidator.setXsrfTokenInRequestAttribute(mockRequest, xsrfToken));
        assertEquals(xsrfToken, mockRequest.getAttribute(requestKey));
    }

    /** Token save succeeds on proper session/request */
    @Test
    public void testSaveTokenSucceeds() {
        assertNotNull(xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession));
    }

    /** Token is not re-generated once set for the session */
    @Test
    public void testSaveTokenReusesTokenStoredInSession() {
        assertNotNull(xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession));
        String token = xsrfTokenValidator.getXsrfTokenFromSession(mockSession);
        assertNotNull(token);
        assertFalse("Token is empty", token.trim().isEmpty());
        String token2 = xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        assertEquals(token, token2);
    }

    /** New token created when session resets. */
    @Test
    public void testSaveTokenCreatesNewTokenForNewSession() {
        String oldXsrfToken = xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        String oldSessionToken = xsrfTokenValidator.getXsrfTokenFromSession(mockSession);
        assertNotNull(oldSessionToken);
        mockSession.invalidate();
        mockSession = new MockHttpSession(mockServletContext);
        String newXsrfToken = xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        String newSessionToken = xsrfTokenValidator.getXsrfTokenFromSession(mockSession);
        assertEquals(oldSessionToken, oldXsrfToken);
        assertEquals(newSessionToken, newXsrfToken);
        assertNotEquals("Old and new tokens are the same!", oldSessionToken, newSessionToken);
        assertNotEquals("Old and new tokens are the same!", oldXsrfToken, newXsrfToken);
    }

    /** Token save fails on null session/request */
    @Test
    public void testSaveTokenFailsOnEmptySessionOrRequest() {
        assertNull(xsrfTokenValidator.saveXsrfToken(null, null));
        assertNull(xsrfTokenValidator.saveXsrfToken(null, mockSession));
        assertNull(xsrfTokenValidator.saveXsrfToken(mockRequest, null));
    }

    /** Validation returns the correct status on success */
    @Test
    public void testValidateXsrfTokenReturnsValidated() {
        xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        String xsrfToken = mockRequest.getAttribute(XsrfTokenValidator.XSRF_TOKEN_REQUEST_ATTR_KEY).toString();
        XsrfTokenStatus status = xsrfTokenValidator.validateXsrfToken(mockSession, xsrfToken);
        assertEquals(XsrfTokenStatus.VALIDATED, status);
    }

    /** Validation returns the correct invalid status with bad xsrf token */
    @Test
    public void testValidateXsrfTokenReturnsInvalid() {
        xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        String xsrfToken = "moose";
        XsrfTokenStatus status = xsrfTokenValidator.validateXsrfToken(mockSession, xsrfToken);
        assertEquals(XsrfTokenStatus.INVALID_XSRF_TOKEN, status);
    }

    /** Validation returns the correct status with empty input */
    @Test
    public void testValidateXsrfTokenReturnsEmptyXsrfToken() {
        xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        XsrfTokenStatus status = xsrfTokenValidator.validateXsrfToken(mockSession, null);
        assertEquals(XsrfTokenStatus.EMPTY_XSRF_TOKEN, status);
    }

    /** Validation returns the correct status with empty session */
    @Test
    public void testValidateXsrfTokenReturnsEmptySession() {
        xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        XsrfTokenStatus status = xsrfTokenValidator.validateXsrfToken(null, "moose");
        assertEquals(XsrfTokenStatus.EMPTY_SESSION, status);
    }

    /** Validation returns the correct status with empty session attribute */
    @Test
    public void testValidateXsrfTokenReturnsEmptySessionXsrf() {
        xsrfTokenValidator.saveXsrfToken(mockRequest, mockSession);
        mockSession.removeAttribute(XsrfTokenValidator.XSRF_TOKEN_SESSION_KEY);
        XsrfTokenStatus status = xsrfTokenValidator.validateXsrfToken(mockSession, "moose");
        assertEquals(XsrfTokenStatus.EMPTY_XSRF_SESSION_TOKEN, status);
    }
}
