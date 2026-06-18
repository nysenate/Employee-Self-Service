package gov.nysenate.ess.web.security;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken.REDMINE_PRINCIPAL;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class RedmineKeyAuthenticationTokenTest {

    @Test
    public void testTokenCreation() {
        String apiKey = "test-api-key-12345";
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(apiKey);
        
        assertEquals(REDMINE_PRINCIPAL, token.getPrincipal());
        assertEquals(apiKey, token.getCredentials());
        assertEquals(apiKey, token.apiKey());
    }

    @Test
    public void testTokenWithNullKey() {
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(null);
        
        assertEquals(REDMINE_PRINCIPAL, token.getPrincipal());
        assertNull(token.getCredentials());
        assertNull(token.apiKey());
    }

    @Test
    public void testTokenWithEmptyKey() {
        String apiKey = "";
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(apiKey);
        
        assertEquals(REDMINE_PRINCIPAL, token.getPrincipal());
        assertEquals(apiKey, token.getCredentials());
        assertEquals(apiKey, token.apiKey());
    }

    @Test
    public void testToString() {
        String apiKey = "test-api-key-12345";
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(apiKey);
        
        String tokenString = token.toString();
        assertTrue(tokenString.contains("RedmineKeyAuthenticationToken"));
        assertTrue(tokenString.contains(REDMINE_PRINCIPAL));
    }
}