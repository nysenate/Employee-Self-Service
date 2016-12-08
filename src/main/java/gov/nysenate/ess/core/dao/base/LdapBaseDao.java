package gov.nysenate.ess.core.dao.base;

import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

import javax.annotation.PostConstruct;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

public abstract class LdapBaseDao
{
    private static final Logger logger = LoggerFactory.getLogger(LdapBaseDao.class);

    @Autowired protected LdapTemplate ldapTemplate;

    @Value("${ldap.base}") private String ldapBase;

    private LdapName baseDn;

    @PostConstruct
    public void init() throws InvalidNameException {
        this.baseDn = new LdapName(ldapBase);
    }

    /**
     * Maps a qualified Distinguished Name to a SenateLdapPerson object.
     * @param dn Name
     * @return SenateLdapPerson that matched the dn
     * @throws NamingException if name is not found
     */
    public SenateLdapPerson getPerson(LdapName dn) throws NamingException {
        return ldapTemplate.findByDn(relativeToBase(dn), SenateLdapPerson.class);
    }

    /**
     * Retrieve a SenateLdapPerson by uid.
     * @param uid String
     * @return SenateLdapPerson
     * @throws NamingException
     */
    public SenateLdapPerson getPersonByUid(String uid) throws NamingException {
        List<SenateLdapPerson> persons = ldapTemplate.search(query().where("uid").is(uid),
                (AttributesMapper<SenateLdapPerson>) SenateLdapPerson::new);
        return persons.get(0);
    }

    public SenateLdapPerson getPersonByEmpId(int empId) throws NamingException {
        List<SenateLdapPerson> peoples = ldapTemplate.search(query().where("employeeid").is(Integer.toString(empId)),
                (AttributesMapper<SenateLdapPerson>) SenateLdapPerson::new);
        return peoples.get(0);
    }

    /**
     * The search method on the LdapTemplate typically returns lists of Objects. In the case when a List of
     * Name objects need to be mapped to a single Name, this method will either return the Name if the list size
     * is 1, or throw an exception indicating that the list is empty or contained more than 1 Name.
     * @param nameList List<Name>
     * @return Name if nameList size is 1.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     */
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

    /* --- Internal Methods --- */

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
}
