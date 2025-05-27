package gov.nysenate.ess.core.dao.security.authentication;

import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Repository;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Provides access to LDAP functionality for authenticating users and retrieving data model
 * objects that represent LDAP records.
 */
@Repository
public class LdapAuthDao implements LdapDao
{
    private LdapName baseDn;
    protected LdapTemplate ldapTemplate;

    @Autowired
    public LdapAuthDao(LdapTemplate ldapTemplate,
                       @Value("${ldap.base:}") String ldapBase) throws InvalidNameException {
        this.ldapTemplate = ldapTemplate;
        this.baseDn = new LdapName(ldapBase);
    }

    /** {@inheritDoc} */
    @Override
    public LdapName authenticateByUid(String uid, String credentials) throws NamingException,
                                                                         IncorrectResultSizeDataAccessException {
        return ldapTemplate.authenticate(getAuthQuery(uid), credentials, contextMapper);
    }

    /** {@inheritDoc} */
    @Override
    public SenateLdapPerson getPerson(LdapName dn) throws NamingException {
        return ldapTemplate.findByDn(relativeToBase(dn), SenateLdapPerson.class);
    }

    /** {@inheritDoc} */
    @Override
    public SenateLdapPerson getPersonByUid(String uid) throws NamingException {
        List<SenateLdapPerson> persons = ldapTemplate.search(query().where("uid").is(uid),
                (AttributesMapper<SenateLdapPerson>) SenateLdapPerson::new);
        return persons.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public SenateLdapPerson getPersonByEmpId(int empId) throws NamingException {
        List<SenateLdapPerson> peoples = ldapTemplate.search(query().where("employeeid").is(Integer.toString(empId)),
                (AttributesMapper<SenateLdapPerson>) SenateLdapPerson::new);
        return peoples.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public Name getNameFromList(List<Name> nameList) throws IncorrectResultSizeDataAccessException {
        if (nameList != null) {
            if (nameList.size() == 1) {
                return nameList.get(0);
            }
            else if (nameList.size() > 1) {
                throw new IncorrectResultSizeDataAccessException("Failed to retrieve match based on uid. Multiple results", 1);
            }
        }
        throw new EmptyResultDataAccessException("Failed to retrieve match based on uid. No results.", 1);
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

    /**
     * Get the given name relative to the base dn
     * @param dn Name
     * @return LdapName
     */
    private LdapName relativeToBase(LdapName dn) {
        if (!dn.startsWith(baseDn)) {
            return dn;
        }
        return (LdapName) dn.getSuffix(baseDn.size());
    }

    private static final AuthenticatedLdapEntryContextMapper<LdapName> contextMapper =
            (ctx, ldapEntryIdentification) -> ldapEntryIdentification.getAbsoluteName();
}