package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.*;
import gov.nysenate.ess.core.util.RangeUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the employees that are managed by the certain supervisor.
 * Note: The group of employees are for T&A purposes only and do not necessarily reflect
 * organizational hierarchy.
 */
public class SupervisorEmpGroup
{
    /** The employee id of the supervisor this group is associated with. */
    protected int supervisorId;

    /** Employees were under this supervisor on/after this date. */
    protected LocalDate startDate;

    /** Employees were under this supervisor before/on this date. */
    protected LocalDate endDate;

    /** Primary employees that directly assigned to this supervisor.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Multimap<Integer, EmployeeSupInfo> primaryEmployees = HashMultimap.create();

    /** Override employees are specific employees that this supervisor was given access to.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Multimap<Integer, EmployeeSupInfo> overrideEmployees = HashMultimap.create();

    /** Supervisor override employees are all the primary employees for the supervisors that
     *  granted override access.
     *  Mapping of the (override granter supId, empId) -> EmployeeInfo */
    protected Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees = HashBasedTable.create();

    /** --- Constructors --- */

    public SupervisorEmpGroup() {}

    public SupervisorEmpGroup(SupervisorEmpGroup supEmpGroup) {
        if (supEmpGroup != null) {
            this.supervisorId = supEmpGroup.supervisorId;
            this.startDate = supEmpGroup.startDate;
            this.endDate = supEmpGroup.endDate;
            this.primaryEmployees = HashMultimap.create(supEmpGroup.primaryEmployees);
            this.overrideEmployees = HashMultimap.create(supEmpGroup.overrideEmployees);
            this.supOverrideEmployees = HashBasedTable.create(supEmpGroup.supOverrideEmployees);
        }
    }

    public SupervisorEmpGroup(int supervisorId, LocalDate startDate, LocalDate endDate) {
        this.supervisorId = supervisorId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** --- Methods --- */

    /**
     * @return true if any employees are stored, false otherwise.
     */
    public boolean hasEmployees() {
        return (primaryEmployees != null && !primaryEmployees.isEmpty()) ||
               (overrideEmployees != null && !overrideEmployees.isEmpty()) ||
               (supOverrideEmployees != null && !supOverrideEmployees.isEmpty());
    }

    public boolean hasEmployeeAtDate(int empId, LocalDate date) {
        return hasEmployeeDuringRange(empId, Range.singleton(date));
    }

    public boolean hasEmployeeDuringRange(int empId, Range<LocalDate> dateRange) {
        Optional<EmployeeSupInfo> supInfoOpt = getDirectEmployeeSupInfos().stream()
                .filter(supInfo -> empId == supInfo.getEmpId())
                .filter(employeeSupInfo -> isSupInfoInRange(employeeSupInfo, dateRange))
                .findAny();
        return supInfoOpt.isPresent();
    }

    /**
     * Filter out any employees in this Supervisor emp group that are not under this supervisor during the
     * given dateRange.
     * @param dateRange Range<LocalDate>
     */
    public void filterActiveEmployeesByDate(Range<LocalDate> dateRange) {

        Range<LocalDate> currentDateRange = Range.closedOpen(startDate, endDate);
        // Do nothing if the new date range covers the existing one
        if (dateRange.encloses(currentDateRange)) {
            return;
        }

        HashMultimap<Integer, EmployeeSupInfo> newPrimaryEmpMap = HashMultimap.create();
        this.getPrimaryEmployees().values().stream()
            .filter(supInfo -> isSupInfoInRange(supInfo, dateRange))
            .forEach(supInfo -> newPrimaryEmpMap.put(supInfo.empId, supInfo));
        this.setPrimaryEmployees(newPrimaryEmpMap);

        HashMultimap<Integer, EmployeeSupInfo> newOverrideEmpMap = HashMultimap.create();
        this.getOverrideEmployees().values().stream()
                .filter(supInfo -> isSupInfoInRange(supInfo, dateRange))
                .forEach(supInfo -> newOverrideEmpMap.put(supInfo.empId, supInfo));
        this.setOverrideEmployees(newOverrideEmpMap);

        HashBasedTable<Integer, Integer, EmployeeSupInfo> filteredSupOverrideEmps = HashBasedTable.create();
        this.supOverrideEmployees.values().stream().filter(supInfo -> isSupInfoInRange(supInfo, dateRange))
            .forEach(supInfo -> filteredSupOverrideEmps.put(supInfo.getSupId(), supInfo.getEmpId(), supInfo));
        this.supOverrideEmployees = filteredSupOverrideEmps;
    }

    /**
     * Determines if an EmployeeSupInfo is contained within the given date range.
     * @param supInfo EmployeeSupInfo
     * @param dateRange Range<LocalDate>
     * @return boolean
     */
    private boolean isSupInfoInRange(EmployeeSupInfo supInfo, Range<LocalDate> dateRange) {
        return RangeUtils.intersects(dateRange, supInfo.getEffectiveDateRange());
    }

    /** --- Functional Getters/Setters --- */

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

    public ImmutableMultimap<Integer, EmployeeSupInfo> getPrimaryEmployees() {
        return ImmutableMultimap.copyOf(primaryEmployees);
    }

    public ImmutableSet<EmployeeSupInfo> getPrimaryEmpSupInfos() {
        return ImmutableSet.copyOf(primaryEmployees.values());
    }

    public void setPrimaryEmployees(Multimap<Integer, EmployeeSupInfo> primaryEmployees) {
        this.primaryEmployees = HashMultimap.create(primaryEmployees);
    }

    public ImmutableMultimap<Integer, EmployeeSupInfo> getOverrideEmployees() {
        return ImmutableMultimap.copyOf(overrideEmployees);
    }

    public void setOverrideEmployees(Multimap<Integer, EmployeeSupInfo> overrideEmployees) {
        this.overrideEmployees = HashMultimap.create(overrideEmployees);
    }

    /** --- Basic Getters/Setters --- */

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Table<Integer, Integer, EmployeeSupInfo> getSupOverrideEmployees() {
        return supOverrideEmployees;
    }

    public void setSupOverrideEmployees(Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees) {
        this.supOverrideEmployees = supOverrideEmployees;
    }
}