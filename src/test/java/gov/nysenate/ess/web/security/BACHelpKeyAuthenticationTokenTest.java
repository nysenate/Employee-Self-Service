package gov.nysenate.ess.web.security;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken.BACHELP_PRINCIPAL;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class BACHelpKeyAuthenticationTokenTest {

    @Test
    public void testTokenCreation() {
        String apiKey = "test-api-key-12345";
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(apiKey);
        
        assertEquals(BACHELP_PRINCIPAL, token.getPrincipal());
        assertEquals(apiKey, token.getCredentials());
        assertEquals(apiKey, token.apiKey());
    }

    @Test
    public void testTokenWithNullKey() {
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(null);
        
        assertEquals(BACHELP_PRINCIPAL, token.getPrincipal());
        assertNull(token.getCredentials());
        assertNull(token.apiKey());
    }

    @Test
    public void testTokenWithEmptyKey() {
        String apiKey = "";
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(apiKey);
        
        assertEquals(BACHELP_PRINCIPAL, token.getPrincipal());
        assertEquals(apiKey, token.getCredentials());
        assertEquals(apiKey, token.apiKey());
    }

    @Test
    public void testToString() {
        String apiKey = "test-api-key-12345";
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(apiKey);
        
        String tokenString = token.toString();
        assertTrue(tokenString.contains("BACHelpKeyAuthenticationToken"));
        assertTrue(tokenString.contains(BACHELP_PRINCIPAL));
    }
}