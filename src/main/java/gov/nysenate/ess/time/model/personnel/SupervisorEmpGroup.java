package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the employees that are managed by the certain supervisor.
 * Note: The group of employees are for T&A purposes only and do not necessarily reflect
 * organizational hierarchy.
 */
public class SupervisorEmpGroup extends PrimarySupEmpGroup
{

    /** Override employees are specific employees that this supervisor was given access to.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Multimap<Integer, EmployeeSupInfo> overrideEmployees = HashMultimap.create();

    /** Supervisor override employees are all the primary employees for the supervisors that
     *  granted override access.
     *  Mapping of the (override granter supId, empId) -> EmployeeInfo */
    protected Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees = HashBasedTable.create();

    public SupervisorEmpGroup() {
        super();
    }

    public SupervisorEmpGroup(PrimarySupEmpGroup primarySupEmpGroup) {
        super(primarySupEmpGroup);
    }

    public SupervisorEmpGroup(SupervisorEmpGroup supEmpGroup) {
        super(supEmpGroup);
        this.overrideEmployees = HashMultimap.create(supEmpGroup.overrideEmployees);
        this.supOverrideEmployees = HashBasedTable.create(supEmpGroup.supOverrideEmployees);
    }

    /* --- Functional Getters / Setters --- */

    public Set<Integer> getOverrideSupIds() {
        return supOverrideEmployees.rowKeySet();
    }

    /**
     * Get all employee sup info's for which the supervisor is responsible for
     * @return {@link Set<EmployeeSupInfo>}
     */
    public ImmutableSet<EmployeeSupInfo> getDirectEmployeeSupInfos() {
        Set<EmployeeSupInfo> empSet = new HashSet<>();
        this.primaryEmployees.values().forEach(empSet::add);
        this.overrideEmployees.values().forEach(empSet::add);
        this.supOverrideEmployees.values().forEach(empSet::add);
        return ImmutableSet.copyOf(empSet);
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

    /**
     * Get overridden employees granted by the given supervisor id
     */
    public Map<Integer, EmployeeSupInfo> getSupOverrideEmployees(int supId) {
        return supOverrideEmployees.rowMap().get(supId);
    }

    public ImmutableMultimap<Integer, EmployeeSupInfo> getOverrideEmployees() {
        return ImmutableMultimap.copyOf(overrideEmployees);
    }

    public void setOverrideEmployees(Multimap<Integer, EmployeeSupInfo> overrideEmployees) {
        this.overrideEmployees = HashMultimap.create(overrideEmployees);
    }

    public void addSupOverrideEmployee(EmployeeSupInfo empSupInfo) {
        this.supOverrideEmployees.put(empSupInfo.getSupId(), empSupInfo.getEmpId(), empSupInfo);
    }

    public void addOverrideEmployee(EmployeeSupInfo employeeSupInfo) {
        this.overrideEmployees.put(employeeSupInfo.getSupId(), employeeSupInfo);
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
                anySupInfoInRange(this.supOverrideEmployees.column(empId).values(), dateRange);
    }

    @Override
    public void filterEmpInfos() {
        super.filterEmpInfos();

        Table<Integer, Integer, EmployeeSupInfo> newSupOverrideEmps = HashBasedTable.create();
        filterEmployeeSupInfos(supOverrideEmployees.values())
                .forEach(esi -> newSupOverrideEmps.put(esi.getSupId(), esi.getEmpId(), esi));
        this.supOverrideEmployees = newSupOverrideEmps;

        Multimap<Integer, EmployeeSupInfo> newOverrideEmps = HashMultimap.create();
        filterEmployeeSupInfos(overrideEmployees.values())
                .forEach(esi -> newOverrideEmps.put(esi.getEmpId(), esi));
        this.overrideEmployees = newOverrideEmps;
    }

    /* --- Basic Getters / Setters --- */

    public Table<Integer, Integer, EmployeeSupInfo> getSupOverrideEmployees() {
        return supOverrideEmployees;
    }

    public void setSupOverrideEmployees(Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees) {
        this.supOverrideEmployees = supOverrideEmployees;
    }
}