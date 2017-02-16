package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.time.LocalDate;
import java.util.*;

/**
 * A {@link SupervisorEmpGroup} that also contains {@link SupervisorEmpGroup}s for each supervisor
 * directly under the target supervisor
 */
public class ExtendedSupEmpGroup extends SupervisorEmpGroup {

    /**
     * {@link SupervisorEmpGroup}s for each direct employee that is a supervisor
     */
    private Multimap<Integer, SupervisorEmpGroup> employeeSupEmpGroups = HashMultimap.create();

    /**
     * Initialize based on a supervisor emp group
     * @param empGroup {@link SupervisorEmpGroup}
     */
    public ExtendedSupEmpGroup(SupervisorEmpGroup empGroup) {
        super(empGroup);
    }

    /* --- Functional Getters / Setters --- */

    public ImmutableMultimap<Integer, SupervisorEmpGroup> getEmployeeSupEmpGroups() {
        return ImmutableMultimap.copyOf(employeeSupEmpGroups);
    }

    public void setEmployeeSupEmpGroups(Multimap<Integer, SupervisorEmpGroup> employeeSupEmpGroups) {
        this.employeeSupEmpGroups = HashMultimap.create(employeeSupEmpGroups);
    }

    public void addEmployeeSupEmpGroup(SupervisorEmpGroup supervisorEmpGroup) {
        employeeSupEmpGroups.put(supervisorEmpGroup.getSupervisorId(), supervisorEmpGroup);
    }

    /**
     * Get {@link EmployeeSupInfo}s for all direct and indirect employees
     * @return {@link Set<EmployeeSupInfo>}
     */
    public Set<EmployeeSupInfo> getExtendedEmployeeSupInfos() {
        Set<EmployeeSupInfo> employeeSupInfos = new HashSet<>(getDirectEmployeeSupInfos());
        this.employeeSupEmpGroups.values().stream()
                .map(SupervisorEmpGroup::getPrimaryEmpSupInfos)
                .flatMap(Collection::stream)
                .forEach(employeeSupInfos::add);
        return employeeSupInfos;
    }
}
