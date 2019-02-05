package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link SupervisorEmpGroup} that also contains {@link SecondarySupEmpGroup}s for each supervisor
 * directly under the target supervisor
 */
public class ExtendedSupEmpGroup extends SupervisorEmpGroup {

    /**
     * {@link SecondarySupEmpGroup}s for each direct employee that is a supervisor
     */
    private Multimap<Integer, SecondarySupEmpGroup> employeeSupEmpGroups = HashMultimap.create();

    /**
     * Initialize based on a supervisor emp group
     * @param empGroup {@link SupervisorEmpGroup}
     */
    public ExtendedSupEmpGroup(SupervisorEmpGroup empGroup) {
        super(empGroup);
    }

    /* --- Functional Getters / Setters --- */

    public ImmutableMultimap<Integer, SecondarySupEmpGroup> getEmployeeSupEmpGroups() {
        return ImmutableMultimap.copyOf(employeeSupEmpGroups);
    }

    public void setEmployeeSupEmpGroups(Multimap<Integer, SecondarySupEmpGroup> employeeSupEmpGroups) {
        this.employeeSupEmpGroups = HashMultimap.create(employeeSupEmpGroups);
    }

    public void addEmployeeSupEmpGroup(SecondarySupEmpGroup supervisorEmpGroup) {
        employeeSupEmpGroups.put(supervisorEmpGroup.getSupervisorId(), supervisorEmpGroup);
    }

    /**
     * Get {@link EmployeeSupInfo}s for all direct and indirect employees
     * @return {@link Set<EmployeeSupInfo>}
     */
    public ImmutableSet<EmployeeSupInfo> getExtendedEmployeeSupInfos() {
        Set<EmployeeSupInfo> employeeSupInfos = new HashSet<>(getDirectEmployeeSupInfos());
        this.employeeSupEmpGroups.values().stream()
                .map(PrimarySupEmpGroup::getPrimaryEmpSupInfos)
                .flatMap(Collection::stream)
                .forEach(employeeSupInfos::add);
        return ImmutableSet.copyOf(employeeSupInfos);
    }

    /**
     * Tests whether an employee was a direct or 1 level indirect employee of this supervisor,
     * during the given date range
     *
     * @param empId int - employee id
     * @param dateRange Range<LocalDate> - date range
     * @return boolean
     */
    public boolean hasExtEmployeeDuringRange(int empId, Range<LocalDate> dateRange) {
        if (hasEmployeeDuringRange(empId, dateRange)) {
            return true;
        }
        return employeeSupEmpGroups.values().stream()
                .anyMatch(empGroup -> empGroup.hasEmployeeDuringRange(empId, dateRange));
    }

}
