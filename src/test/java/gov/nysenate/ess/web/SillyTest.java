package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.dao.security.authentication.LdapAuthDao;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * A sample file to run misc tests.
 */
@Category(gov.nysenate.ess.core.annotation.SillyTest.class)
public class SillyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SillyTest.class);

    @Autowired LdapAuthDao ldapAuthDao;
    @Autowired EmployeeInfoService employeeService;

    @Test
    public void tst() {

        Set<Employee> emps = employeeService.getAllEmployees(true);

        System.out.println("hell");
//        Set<SenateLdapPerson> ldapPeople = new HashSet<>();
//        Set<Employee> emps = employeeService.getAllEmployees(true);
//        for (Employee e : emps) {
//            try {
//                ldapPeople.add(ldapAuthDao.getPersonByEmpId(e.getEmployeeId()));
//            } catch(Exception ex) {
//                System.out.println("Failed to get ldap infor for empid: " + e.getEmployeeId());
//            }
//        }

//        for (String dep : departments) {
//            if (!dep.startsWith("Senator")) {
//                System.out.println(dep);
//            }
//        }
   }
}