package gov.nysenate.ess.web.security.realm;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.util.ReflectionTestUtils;

import static gov.nysenate.ess.web.security.BACHelpKeyAuthenticationToken.BACHELP_PRINCIPAL;
import static org.junit.Assert.*;

@Category(UnitTest.class)
public class EssBACHelpKeyRealmTest {

    private EssBACHelpKeyRealm realm;
    private static final String VALID_API_KEY = "valid-test-api-key-12345678901234567890";
    private static final String INVALID_API_KEY = "invalid-key";

    @Before
    public void setUp() {
        realm = new EssBACHelpKeyRealm();
        
        // Set configuration properties using reflection
        ReflectionTestUtils.setField(realm, "bachelpAuthEnabled", true);
        ReflectionTestUtils.setField(realm, "bachelpApiKey", VALID_API_KEY);
    }

    @Test
    public void testSupportsCorrectTokenClass() {
        assertEquals(BACHelpKeyAuthenticationToken.class, realm.getAuthenticationTokenClass());
    }

    @Test
    public void testValidAuthentication() throws AuthenticationException {
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(VALID_API_KEY);
        
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(token);
        
        assertNotNull(authInfo);
        assertEquals(BACHELP_PRINCIPAL, authInfo.getPrincipals().getPrimaryPrincipal());
        assertEquals(VALID_API_KEY, authInfo.getCredentials());
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testInvalidAuthentication() throws AuthenticationException {
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(INVALID_API_KEY);
        
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testEmptyKeyAuthentication() throws AuthenticationException {
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken("");
        
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = IncorrectCredentialsException.class)
    public void testNullKeyAuthentication() throws AuthenticationException {
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(null);
        
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = DisabledAccountException.class)
    public void testDisabledAuthentication() throws AuthenticationException {
        ReflectionTestUtils.setField(realm, "bachelpAuthEnabled", false);
        
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(VALID_API_KEY);
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = AuthenticationException.class)
    public void testUnconfiguredApiKey() throws AuthenticationException {
        ReflectionTestUtils.setField(realm, "bachelpApiKey", "");
        
        BACHelpKeyAuthenticationToken token = new BACHelpKeyAuthenticationToken(VALID_API_KEY);
        realm.doGetAuthenticationInfo(token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullToken() throws AuthenticationException {
        realm.doGetAuthenticationInfo(null);
    }

    @Test
    public void testAuthorizationForValidPrincipal() {
        PrincipalCollection principals = new SimplePrincipalCollection(BACHELP_PRINCIPAL, realm.getName());
        
        AuthorizationInfo authInfo = realm.doGetAuthorizationInfo(principals);
        
        assertNotNull(authInfo);
        assertTrue(authInfo.getObjectPermissions().contains(SimpleEssPermission.BACHELP_API_ACCESS.getPermission()));
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