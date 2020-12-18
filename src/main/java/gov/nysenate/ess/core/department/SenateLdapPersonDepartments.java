package gov.nysenate.ess.core.department;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.core.model.auth.SenateLdapPerson;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SenateLdapPersonDepartments {

    /**
     * Creates a LdapDepartment for each distinct department in {@code people}.
     * @param people
     * @return
     */
    public static Set<LdapDepartment> forPeople(Collection<SenateLdapPerson> people) {
        Multimap<String, Integer> deptNameToEmpId = HashMultimap.create();
        for (SenateLdapPerson p : people) {
            deptNameToEmpId.put(p.getDepartment(), p.getEmployeeId());
        }

        Set<LdapDepartment> departments = new HashSet<>();
        for (String name : deptNameToEmpId.keys()) {
            departments.add(new LdapDepartment(name, deptNameToEmpId.get(name)));
        }
        return departments;
    }
}
