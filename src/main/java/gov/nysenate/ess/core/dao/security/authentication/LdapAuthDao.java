package gov.nysenate.ess.core.dao.security.authentication;

import gov.nysenate.ess.core.dao.base.LdapBaseDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Repository;

import javax.naming.ldap.LdapName;

/**
 * Provides access to LDAP functionality for authenticating users and retrieving data model
 * objects that represent LDAP records.
 */
@Repository
public class LdapAuthDao extends LdapBaseDao
{

    @Value("${ldap.base:}")
    private String ldapBase;

   /**
     * Authenticates uid via a simple LDAP bind. If the bind was successful the matching Name will be returned.
     * Otherwise an error will have been thrown indicating the exception.
    *
     * @param uid String (username)
     * @param credentials String (password)
     * @return Name representing authenticated user. Otherwise exception will be thrown.
     * @throws AuthenticationException given invalid credentials
     * @throws NamingException general exception
     * @throws IncorrectResultSizeDataAccessException potential multiple matches
   */
    public LdapName authenticateByUid(String uid, String credentials) throws NamingException,
                                                                         IncorrectResultSizeDataAccessException {
        return ldapTemplate.authenticate(getAuthQuery(uid), credentials, contextMapper);
    }

    /**
     * Return an {@link LdapQuery} that will authenticate the user with the given uid
     * @param uid String - User's uid
     * @return {@link LdapQuery}
     */
    private LdapQuery getAuthQuery(String uid) {
        return LdapQueryBuilder.query()
//                .base(ldapBase)
                .where("uid").is(uid)
                ;
    }

    private static final AuthenticatedLdapEntryContextMapper<LdapName> contextMapper =
            (ctx, ldapEntryIdentification) -> ldapEntryIdentification.getAbsoluteName();
}