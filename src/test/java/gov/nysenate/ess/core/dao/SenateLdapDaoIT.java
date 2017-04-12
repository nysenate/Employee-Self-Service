package gov.nysenate.ess.core.dao;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.dao.security.authentication.LdapAuthDao;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NameNotFoundException;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Category({IntegrationTest.class, TestDependsOnDatabase.class})
public class SenateLdapDaoIT extends BaseTest
{
    @Autowired LdapAuthDao ldapAuthDao;

    @Value("${test.ldap.valid.uid}") private String validUid;
    @Value("${test.ldap.valid.dn}") private String validRelDn;
    @Value("${test.ldap.valid.password}") private String validPassword;

    @Value("${ldap.base:}") private String ldapBase;
    private LdapName ldapBaseDn;

    private SenateLdapPerson validSenateLdapPerson = new SenateLdapPerson();
    private LdapName invalidLdapName;
    private String invalidUid;

    @Before
    public void setUp() throws Exception {
        ldapBaseDn = new LdapName(ldapBase);

        validSenateLdapPerson.setUid(validUid);

        invalidUid = "moosekitten";
        invalidLdapName = (LdapName) ((LdapName)ldapBaseDn.clone())
                .add(new Rdn("cn", invalidUid));
    }

    @Test
    public void testSenateLdapDaoAutowired() throws Exception {
        assertNotNull(ldapAuthDao);
    }

    /** {@link LdapAuthDao#getPerson(javax.naming.ldap.LdapName)} (String)} tests
     * -------------------------------------------------------------------- */

    @Test
    public void testGetPerson() throws Exception {
        LdapName name = new LdapName(validRelDn);
        SenateLdapPerson person = ldapAuthDao.getPerson(name);
        assertNotNull(person);
        assertEquals(validSenateLdapPerson.getUid(), person.getUid());
    }

    @Test(expected = NameNotFoundException.class)
    public void testGetPersonThrowsNameNotFoundException() throws Exception {
        SenateLdapPerson person = ldapAuthDao.getPerson(invalidLdapName);
    }

    /** {@link LdapAuthDao#authenticateByUid(String, String)} tests
     * ------------------------------------------------------------------*/

    @Test
    public void testAuthenticateByUidSucceeds() throws Exception {
        Name name = ldapAuthDao.authenticateByUid(validUid, validPassword);
        assertNotNull(name);
        assertEquals(new LdapName(validRelDn), name.getSuffix(ldapBaseDn.size()));
        assertEquals(ldapBaseDn, name.getPrefix(ldapBaseDn.size()));
    }

    @Test(expected = AuthenticationException.class)
    public void testAuthenticateByUidWithWrongPassword_throwsAuthenticationException() throws Exception {
        ldapAuthDao.authenticateByUid(validUid, "clearlyAWrongPassword");
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testAuthenticateByUidWithWrongUidAndPassword_throwsEmptyResultException() throws Exception {
        ldapAuthDao.authenticateByUid(invalidUid, "clearlyAWrongPassword");
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testAuthenticateByUidWithNullUidAndPassword_throwsEmptyResultException() throws Exception {
        ldapAuthDao.authenticateByUid(null, null);
    }
}
