package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the employees that are managed by the certain supervisor.
 * Note: The group of employees are for T&A purposes only and do not necessarily reflect
 * organizational hierarchy.
 */
public class SupervisorEmpGroup extends PrimarySupEmpGroup
{

    /** Collection of employee overrides */
    private Collection<EmployeeSupInfo> overrideEmployees = new LinkedList<>();

    /** Collection of supervisor overrides */
    private Collection<EmployeeSupInfo> supOverrideEmployees = new LinkedList<>();

    public SupervisorEmpGroup() {
        super();
    }

    public SupervisorEmpGroup(PrimarySupEmpGroup primarySupEmpGroup) {
        super(primarySupEmpGroup);
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public SupervisorEmpGroup(SupervisorEmpGroup supEmpGroup) {
        super(supEmpGroup);
        this.overrideEmployees = new LinkedList<>(supEmpGroup.overrideEmployees);
        this.supOverrideEmployees = new LinkedList<>(supEmpGroup.supOverrideEmployees);
    }

    /* --- Functional Getters / Setters --- */

    /**
     * Get all employee sup info's for which the supervisor is responsible for
     * @return {@link Set<EmployeeSupInfo>}
     */
    public ImmutableSet<EmployeeSupInfo> getDirectEmployeeSupInfos() {
        return ImmutableSet.<EmployeeSupInfo>builder()
                .addAll(this.primaryEmployees.values())
                .addAll(this.overrideEmployees)
                .addAll(this.supOverrideEmployees)
                .build();
    }

    /**
     * Get all employee ids for which the supervisor is responsible for
     * @return {@link Set<Integer>}
     */
    public ImmutableSet<Integer> getDirectEmpIds() {
        return getDirectEmployeeSupInfos().stream()
                .map(EmployeeSupInfo::getEmpId)
                .collect(Collectors.collectingAndThen(
                        Collectors.toSet(), ImmutableSet::copyOf
                ));
    }

    public ImmutableCollection<EmployeeSupInfo> getOverrideEmployees() {
        return ImmutableList.copyOf(overrideEmployees);
    }

    public ImmutableCollection<EmployeeSupInfo> getSupOverrideInfos() {
        return ImmutableList.copyOf(supOverrideEmployees);
    }

    public void setSupOverrideEmployees(Collection<EmployeeSupInfo> supOverrideEmployees) {
        this.supOverrideEmployees = new LinkedList<>();
        supOverrideEmployees.forEach(this::addSupOverrideEmployee);
    }

    public void addSupOverrideEmployee(EmployeeSupInfo empSupInfo) {
        // Prevent the supervisor from getting themselves as an employee
        // This happens often when an employee is given an override for their own supervisor
        if (empSupInfo.getEmpId() == this.supervisorId) {
            return;
        }
        supOverrideEmployees.add(empSupInfo);
    }

    public void setOverrideEmployees(Collection<EmployeeSupInfo> overrideEmployees) {
        this.overrideEmployees = new LinkedList<>();
        overrideEmployees.forEach(this::addOverrideEmployee);
    }

    public void addOverrideEmployee(EmployeeSupInfo employeeSupInfo) {
        this.overrideEmployees.add(employeeSupInfo);
    }

    /* --- Overrides --- */

    @Override
    public boolean hasEmployees() {
        return super.hasEmployees() ||
                (overrideEmployees != null && !overrideEmployees.isEmpty()) ||
                (supOverrideEmployees != null && !supOverrideEmployees.isEmpty());
    }

    @Override
    public boolean hasEmployeeDuringRange(int empId, Range<LocalDate> dateRange) {
        return super.hasEmployeeDuringRange(empId, dateRange) ||
                anySupInfoInRange(this.overrideEmployees, dateRange) ||
                anySupInfoInRange(this.supOverrideEmployees, dateRange);
    }

    @Override
    public void filterEmpInfos() {
        super.filterEmpInfos();

        setSupOverrideEmployees(filterEmployeeSupInfos(supOverrideEmployees));
        setOverrideEmployees(filterEmployeeSupInfos(overrideEmployees));
    }
}