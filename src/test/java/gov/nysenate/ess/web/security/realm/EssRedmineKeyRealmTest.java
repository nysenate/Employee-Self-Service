package gov.nysenate.ess.web.security.realm;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;

import static gov.nysenate.ess.web.security.RedmineKeyAuthenticationToken.REDMINE_PRINCIPAL;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class EssRedmineKeyRealmTest {

    private EssRedmineKeyRealm realm;
    private static final String VALID_API_KEY = "valid-test-api-key-12345678901234567890";
    private static final String INVALID_API_KEY = "invalid-key";

    @Before
    public void setUp() {
        realm = new EssRedmineKeyRealm();
        
        // Set configuration properties using reflection
        ReflectionTestUtils.setField(realm, "redmineAuthEnabled", true);
        ReflectionTestUtils.setField(realm, "redmineApiKey", VALID_API_KEY);
    }

    @Test
    public void testSupportsCorrectTokenClass() {
        assertEquals(RedmineKeyAuthenticationToken.class, realm.getAuthenticationTokenClass());
    }

    @Test
    public void testValidAuthentication() throws AuthenticationException {
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(VALID_API_KEY);
        
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(token);
        
        assertNotNull(authInfo);
        assertEquals(REDMINE_PRINCIPAL, authInfo.getPrincipals().getPrimaryPrincipal());
        assertEquals(VALID_API_KEY, authInfo.getCredentials());
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testInvalidAuthentication() throws AuthenticationException {
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(INVALID_API_KEY);
        
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testEmptyKeyAuthentication() throws AuthenticationException {
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken("");
        
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testNullKeyAuthentication() throws AuthenticationException {
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(null);
        
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = DisabledAccountException.class)
    public void testDisabledAuthentication() throws AuthenticationException {
        ReflectionTestUtils.setField(realm, "redmineAuthEnabled", false);
        
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(VALID_API_KEY);
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = AuthenticationException.class)
    public void testUnconfiguredApiKey() throws AuthenticationException {
        ReflectionTestUtils.setField(realm, "redmineApiKey", "");
        
        RedmineKeyAuthenticationToken token = new RedmineKeyAuthenticationToken(VALID_API_KEY);
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullToken() throws AuthenticationException {
        realm.doGetAuthenticationInfo(null);
    }

    @Test
    public void testAuthorizationForValidPrincipal() {
        PrincipalCollection principals = new SimplePrincipalCollection(REDMINE_PRINCIPAL, realm.getName());
        
        AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principals);
        
        assertNotNull(authInfo);
        assertTrue(authInfo.getObjectPermissions().contains(SimpleEssPermission.REDMINE_API_ACCESS.getPermission()));
    }

    @Test
    public void testAuthorizationForInvalidPrincipal() {
        PrincipalCollection principals = new SimplePrincipalCollection("invalid-principal", realm.getName());
        
        AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principals);
        
        assertNotNull(authInfo);
        assertTrue(authInfo.getObjectPermissions() == null || authInfo.getObjectPermissions().isEmpty());
    }

    @Test
    public void testAuthorizationWithClassCastException() {
        // Test with Integer instead of String principal
        PrincipalCollection principals = new SimplePrincipalCollection(12345, realm.getName());
        
        AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principals);
        
        assertNotNull(authInfo);
        assertTrue(authInfo.getObjectPermissions() == null || authInfo.getObjectPermissions().isEmpty());
    }
}