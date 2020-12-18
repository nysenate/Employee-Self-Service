package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.ess.core.department.LdapDepartmentFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@org.junit.experimental.categories.Category(UnitTest.class)
public class LdapDepartmentComparerTest {

    /**
     * --- newDepartments tests ---
     */

    @Test
    public void givenSameDepartments_thenNoNewDepartments() {
        Set<LdapDepartment> newDepartments = LdapDepartmentComparer.newDepartments(
                Sets.newHashSet(STS), Sets.newHashSet(STS));
        assertTrue(newDepartments.isEmpty());
    }

    @Test
    public void givenNoLdapDepartments_thenNoNewDepartments() {
        Set<LdapDepartment> newDepartments = LdapDepartmentComparer.newDepartments(
                Sets.newHashSet(STS), new HashSet<>());
        assertTrue(newDepartments.isEmpty());
    }

    @Test
    public void givenNoEssDepartments_thenAllLdapDepartmentsAreNew() {
        Set<LdapDepartment> ldapDepartments = Sets.newHashSet(STS, SUPPLY);
        Set<LdapDepartment> newDepartments = LdapDepartmentComparer.newDepartments(
                Sets.newHashSet(), ldapDepartments);
        assertEquals(ldapDepartments, newDepartments);
    }

    @Test
    public void givenDifferentDepartmentEmployees_thenNoNewDepartments() {
        LdapDepartment essDepartment = new LdapDepartment("STS", Sets.newHashSet(1, 2));
        LdapDepartment ldapDepartment = new LdapDepartment("STS", Sets.newHashSet(1));

        Set<LdapDepartment> newDepartments = LdapDepartmentComparer.newDepartments(
                Sets.newHashSet(essDepartment), Sets.newHashSet(ldapDepartment));
        assertTrue(newDepartments.isEmpty());
    }

    @Test
    public void givenExtraDepartmentInEss_thenNoNewDepartments() {
        Set<LdapDepartment> ldapDepartments = Sets.newHashSet(STS);
        Set<LdapDepartment> newDepartments = LdapDepartmentComparer.newDepartments(
                Sets.newHashSet(STS, SUPPLY), ldapDepartments);
        assertTrue(newDepartments.isEmpty());
    }

    @Test
    public void givenExtraDepartmentInLdap_thenExtraDepartmentIsNew() {
        Set<LdapDepartment> ldapDepartments = Sets.newHashSet(STS, SUPPLY);
        Set<LdapDepartment> newDepartments = LdapDepartmentComparer.newDepartments(
                Sets.newHashSet(STS), ldapDepartments);
        assertEquals(Sets.newHashSet(SUPPLY), newDepartments);
    }

    /**
     * --- inactiveDepartments tests ---
     */

    @Test
    public void givenSameDepartments_thenNoInactiveDepartments() {
        Set<LdapDepartment> inactiveDepartments = LdapDepartmentComparer.inactiveDepartments(
                Sets.newHashSet(STS), Sets.newHashSet(STS));
        assertTrue(inactiveDepartments.isEmpty());
    }

    @Test
    public void givenNoEssDepartments_thenNoInactiveDepartments() {
        Set<LdapDepartment> inactiveDepartments = LdapDepartmentComparer.inactiveDepartments(
                Sets.newHashSet(), Sets.newHashSet(STS));
        assertTrue(inactiveDepartments.isEmpty());
    }

    @Test
    public void givenNoLdapDepartments_thenAllDepartmentsInactive() {
        Set<LdapDepartment> inactiveDepartments = LdapDepartmentComparer.inactiveDepartments(
                Sets.newHashSet(STS, SUPPLY), Sets.newHashSet());
        assertEquals(Sets.newHashSet(STS, SUPPLY), inactiveDepartments);
    }

    @Test
    public void givenDifferentDepartmentEmployees_thenNoDepartmentInactive() {
        LdapDepartment essDepartment = new LdapDepartment("STS", Sets.newHashSet(1, 2));
        LdapDepartment ldapDepartment = new LdapDepartment("STS", Sets.newHashSet(1));

        Set<LdapDepartment> inactiveDepartments = LdapDepartmentComparer.inactiveDepartments(
                Sets.newHashSet(essDepartment), Sets.newHashSet(ldapDepartment));
        assertTrue(inactiveDepartments.isEmpty());
    }

    @Test
    public void givenExtraEssDepartments_thenExtraDepartmentsAreInactive() {
        Set<LdapDepartment> inactiveDepartments = LdapDepartmentComparer.inactiveDepartments(
                Sets.newHashSet(STS, SUPPLY), Sets.newHashSet(STS));
        assertEquals(Sets.newHashSet(SUPPLY), inactiveDepartments);
    }

    @Test
    public void givenExtraLdapDepartments_thenNoInactiveDepartments() {
        Set<LdapDepartment> inactiveDepartments = LdapDepartmentComparer.inactiveDepartments(
                Sets.newHashSet(STS), Sets.newHashSet(STS, SUPPLY));
        assertTrue(inactiveDepartments.isEmpty());
    }

    /**
     * --- updatedDepartments tests ---
     */

    @Test
    public void givenEmptyDepartmentsParam_thenNoUpdatedDepartments() {
        Set<LdapDepartment> updatedDepartments = LdapDepartmentComparer.updatedDepartments(
                Sets.newHashSet(STS), new HashSet<>());
        assertTrue(updatedDepartments.isEmpty());

        updatedDepartments = LdapDepartmentComparer.updatedDepartments(
                Sets.newHashSet(), Sets.newHashSet(STS));
        assertTrue(updatedDepartments.isEmpty());
    }

    @Test
    public void givenSameDepartments_thenNoUpdatedDepartments() {
        Set<LdapDepartment> updatedDepartments = LdapDepartmentComparer.updatedDepartments(
                Sets.newHashSet(STS), Sets.newHashSet(STS));
        assertTrue(updatedDepartments.isEmpty());
    }

    @Test
    public void givenDifferentEmployees_thenDepartmentUpdated() {
        LdapDepartment essDepartment = new LdapDepartment("STS", Sets.newHashSet(1, 2));
        LdapDepartment ldapDepartment = new LdapDepartment("STS", Sets.newHashSet(1));

        Set<LdapDepartment> updatedDepartments = LdapDepartmentComparer.updatedDepartments(
                Sets.newHashSet(essDepartment), Sets.newHashSet(ldapDepartment));

        assertEquals(Sets.newHashSet(ldapDepartment), updatedDepartments);
    }

}
