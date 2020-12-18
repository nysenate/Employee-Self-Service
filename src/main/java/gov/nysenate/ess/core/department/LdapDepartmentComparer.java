package gov.nysenate.ess.core.department;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LdapDepartmentComparer {

    /**
     * Returns LdapDepartments which exist in {@code ldapDepartments} but not in {@code essDepartments}.
     * THis check is done by comparing their {@code name} fields. The {@code employeeIds} field is
     * not checked as part of this because we are only looking for new LdapDepartments,
     * not updated/changed LdapDepartments.
     *
     * @param essDepartments
     * @param ldapDepartments
     */
    public static Set<LdapDepartment> newDepartments(Set<LdapDepartment> essDepartments,
                                                     Set<LdapDepartment> ldapDepartments) {
        Map<String, LdapDepartment> essDepartmentMap = createNameToDeptMap(essDepartments);
        Map<String, LdapDepartment> ldapDepartmentMap = createNameToDeptMap(ldapDepartments);

        Set<String> newDepartmentNames = ldapDepartmentMap.keySet().stream()
                .filter(n -> !essDepartmentMap.containsKey(n))
                .collect(Collectors.toSet());

        return newDepartmentNames.stream()
                .map(ldapDepartmentMap::get)
                .collect(Collectors.toSet());
    }

    /**
     * Returns LdapDepartments which exist in {@code essDepartments} but not in {@code ldapDepartments}.
     * This comparison only checks the LdapDepartment {@code name}, {@code employeeIds} are not
     * checked for equality because we are looking for departments which should be inactivated.
     * @param essDepartments
     * @param ldapDepartments
     * @return
     */
    public static Set<LdapDepartment> inactiveDepartments(Set<LdapDepartment> essDepartments,
                                                          Set<LdapDepartment> ldapDepartments) {
        // The inverse of newDepartments gives us inactiveDepartments.
        return newDepartments(ldapDepartments, essDepartments);
    }

    /**
     * Returns a set of the most up to date LdapDepartments. The returned LdapDepartments
     * replace the corresponding LdapDepartment from {@code essDepartments} where
     * their LdapDepartment.name's are equal.
     *
     * @param essDepartments Previously persisted departments.
     * @param ldapDepartments The most recent listing of departments.
     * @return
     */
    public static Set<LdapDepartment> updatedDepartments(Set<LdapDepartment> essDepartments,
                                                         Set<LdapDepartment> ldapDepartments) {
        Map<String, LdapDepartment> essDepartmentMap = createNameToDeptMap(essDepartments);
        Map<String, LdapDepartment> ldapDepartmentMap = createNameToDeptMap(ldapDepartments);

        Set<String> commonDepartmentNames = Sets.intersection(essDepartmentMap.keySet(), ldapDepartmentMap.keySet());

        Set<LdapDepartment> updatedDepartments = new HashSet<>();
        for (String name : commonDepartmentNames) {
            LdapDepartment essDepartment = essDepartmentMap.get(name);
            LdapDepartment ldapDepartment = ldapDepartmentMap.get(name);
            if (!essDepartment.equals(ldapDepartment)) {
                updatedDepartments.add(ldapDepartment);
            }
        }

        return updatedDepartments;
    }

    private static Map<String, LdapDepartment> createNameToDeptMap(Set<LdapDepartment> departments) {
        return departments.stream()
                .collect(Collectors.toMap(LdapDepartment::getName, Function.identity()));
    }
}
