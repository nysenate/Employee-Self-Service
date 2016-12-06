package gov.nysenate.ess.web;

import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.Name;
import javax.naming.NamingException;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Category(gov.nysenate.ess.core.annotation.SillyTest.class)
public class SpringLdapTest extends WebTests
{
    private static final Logger logger = LoggerFactory.getLogger(SpringLdapTest.class);

    @Autowired
    LdapTemplate ldapTemplate;

    @Test
    public void testLDAP() throws Exception {

        String uid = "";

        List<Name> name = ldapTemplate.search(query().where("uid").is(uid), new ContextMapper<Name>() {
            @Override
            public Name mapFromContext(Object ctx) throws NamingException {
                return ((DirContextAdapter) ctx).getDn();
            }
        });

        if (name != null && name.size() == 1) {

            Name dn = name.get(0);
            SenateLdapPerson person = ldapTemplate.findByDn(dn, SenateLdapPerson.class);
            logger.debug("Emp ID: " + person.getEmployeeId());
            logger.debug("Department: " + person.getDepartment());
            logger.debug("DN: " + person.getDn());
            logger.debug("SN: " + person.getSn());
            logger.debug("UID: " + person.getUid());
            logger.debug("Email: " + person.getEmail());
            logger.debug("Full Name: " + person.getFullName());
            logger.debug("Organization: " + person.getOrganization());
            logger.debug("Title: " + person.getTitle());
            logger.debug("State: " + person.getState());
            logger.debug("Location: " + person.getLocation());
            logger.debug("Office Address: " + person.getOfficeAddress());
            logger.debug("Postal Address: " + person.getPostalAddress());
        }

    }

    @Test
    public void getByEmpIdTest() {
        int empId = 11423;
        List<SenateLdapPerson> peoples = ldapTemplate.search(query().where("employeeid").is(Integer.toString(empId)),
                (AttributesMapper<SenateLdapPerson>) SenateLdapPerson::new);
        logger.info("{}", peoples.get(0));
    }
}
