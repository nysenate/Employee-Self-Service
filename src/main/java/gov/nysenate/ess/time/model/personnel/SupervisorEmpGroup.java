package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.*;

import java.time.LocalDate;
import java.util.Collection;
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
    private Multimap<Integer, EmployeeSupInfo> overrideEmployees = HashMultimap.create();

    /** Collection of supervisor overrides */
    private Multimap<Integer, EmployeeSupInfo> supOverrideEmployees = HashMultimap.create();

    public SupervisorEmpGroup(int supId) {
        super(supId);
    }

    public SupervisorEmpGroup(PrimarySupEmpGroup primarySupEmpGroup) {
        super(primarySupEmpGroup);
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public SupervisorEmpGroup(SupervisorEmpGroup supEmpGroup) {
        super(supEmpGroup);
        this.overrideEmployees = HashMultimap.create(supEmpGroup.overrideEmployees);
        this.supOverrideEmployees = HashMultimap.create(supEmpGroup.supOverrideEmployees);
    }

    /* --- Functional Getters / Setters --- */

    /**
     * Get all employee sup info's for which the supervisor is responsible for
     * @return {@link Set<EmployeeSupInfo>}
     */
    public ImmutableSet<EmployeeSupInfo> getDirectEmployeeSupInfos() {
        return ImmutableSet.<EmployeeSupInfo>builder()
                .addAll(this.primaryEmployees.values())
                .addAll(this.overrideEmployees.values())
                .addAll(this.supOverrideEmployees.values())
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
        return ImmutableList.copyOf(overrideEmployees.values());
    }

    public ImmutableCollection<EmployeeSupInfo> getSupOverrideInfos() {
        return ImmutableList.copyOf(supOverrideEmployees.values());
    }

    public void setSupOverrideEmployees(Collection<EmployeeSupInfo> supOverrideEmployees) {
        this.supOverrideEmployees = HashMultimap.create();
        supOverrideEmployees.forEach(this::addSupOverrideEmployee);
    }

    public void addSupOverrideEmployee(EmployeeSupInfo empSupInfo) {
        // Prevent the supervisor from getting themselves as an employee
        // This happens often when an employee is given an override for their own supervisor
        if (empSupInfo.getEmpId() == this.supervisorId) {
            return;
        }
        supOverrideEmployees.put(empSupInfo.getEmpId(), empSupInfo);
    }

    public void setOverrideEmployees(Collection<EmployeeSupInfo> overrideEmployees) {
        this.overrideEmployees = HashMultimap.create();
        overrideEmployees.forEach(this::addOverrideEmployee);
    }

    public void addOverrideEmployee(EmployeeSupInfo employeeSupInfo) {
        this.overrideEmployees.put(employeeSupInfo.getEmpId(), employeeSupInfo);
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
                anySupInfoInRange(this.overrideEmployees.get(empId), dateRange) ||
                anySupInfoInRange(this.supOverrideEmployees.get(empId), dateRange);
    }

    @Override
    public void filterEmpInfos() {
        super.filterEmpInfos();

        setSupOverrideEmployees(filterEmployeeSupInfos(supOverrideEmployees.values()));
        setOverrideEmployees(filterEmployeeSupInfos(overrideEmployees.values()));
    }
}