package gov.nysenate.ess.core.dao.security.authentication;

import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.List;

public interface LdapDao {

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
    LdapName authenticateByUid(String uid, String credentials)
            throws NamingException, IncorrectResultSizeDataAccessException;

    /**
     * Maps a qualified Distinguished Name to a SenateLdapPerson object.
     * @param dn Name
     * @return SenateLdapPerson that matched the dn
     * @throws NamingException if name is not found
     */
    SenateLdapPerson getPerson(LdapName dn) throws NamingException;

    /**
     * Retrieve a SenateLdapPerson by uid.
     * @param uid String
     * @return SenateLdapPerson
     * @throws NamingException
     */
    SenateLdapPerson getPersonByUid(String uid) throws NamingException;

    SenateLdapPerson getPersonByEmpId(int empId) throws NamingException;

    /**
     * The search method on the LdapTemplate typically returns lists of Objects. In the case when a List of
     * Name objects need to be mapped to a single Name, this method will either return the Name if the list size
     * is 1, or throw an exception indicating that the list is empty or contained more than 1 Name.
     * @param nameList List<Name>
     * @return Name if nameList size is 1.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException
     */
    Name getNameFromList(List<Name> nameList) throws IncorrectResultSizeDataAccessException;
}
