package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.ess.core.department.SenateLdapPersonFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.junit.experimental.categories.Category(UnitTest.class)
public class SenateLdapPersonDepartmentsTest {

    @Test
    public void givenEmptyCollection_returnEmptyCollection() {
        assertTrue(SenateLdapPersonDepartments.forPeople(new HashSet<>()).isEmpty());
    }

    @Test
    public void givenOnePerson_returnTheirLdapDepartment() {
        Set<SenateLdapPerson> people = Sets.newHashSet(LDAP_PERSON_STS_1);
        Set<LdapDepartment> expected = Sets.newHashSet(new LdapDepartment("STS", Sets.newHashSet(1)));
        Set<LdapDepartment> actual = SenateLdapPersonDepartments.forPeople(people);
        assertEquals(expected, actual);
    }

    @Test
    public void givenMultiplePeopleInOneDepartment_returnOneDepartment() {
        Set<SenateLdapPerson> people = Sets.newHashSet(LDAP_PERSON_STS_1, LDAP_PERSON_STS_2);
        Set<LdapDepartment> expected = Sets.newHashSet(new LdapDepartment("STS", Sets.newHashSet(1, 2)));
        Set<LdapDepartment> actual = SenateLdapPersonDepartments.forPeople(people);
        assertTrue(actual.size() == 1);
        assertEquals(expected, actual);
    }

    @Test
    public void givenPeopleInMultipleDepartments_returnTheirDepartments() {
        Set<SenateLdapPerson> people = Sets.newHashSet(LDAP_PERSON_STS_1, LDAP_PERSON_STS_2, LDAP_PERSON_SUPPLY_3);

        LdapDepartment expectedDept1 = new LdapDepartment("STS", Sets.newHashSet(1, 2));
        LdapDepartment expectedDept2 = new LdapDepartment("SUPPLY", Sets.newHashSet(3));
        Set<LdapDepartment> expected = Sets.newHashSet(expectedDept1, expectedDept2);

        Set<LdapDepartment> actual = SenateLdapPersonDepartments.forPeople(people);
        assertEquals(expected, actual);
        assertTrue(actual.size() == 2);
    }
}
