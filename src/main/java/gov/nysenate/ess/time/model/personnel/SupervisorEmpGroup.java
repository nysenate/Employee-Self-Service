package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
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
    protected Map<Integer, EmployeeSupInfo> primaryEmployees = new HashMap<>();

    /** Override employees are specific employees that this supervisor was given access to.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Map<Integer, EmployeeSupInfo> overrideEmployees = new HashMap<>();

    /** Supervisor override employees are all the primary employees for the supervisors that
     *  granted override access.
     *  Mapping of the (override granter supId, empId) -> EmployeeInfo */
    protected Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees = HashBasedTable.create();

    /** --- Constructors --- */

    public SupervisorEmpGroup() {}

    public SupervisorEmpGroup(SupervisorEmpGroup supEmpGroup) {
        if (supEmpGroup != null) {
            this.supervisorId = supEmpGroup.supervisorId;
            this.startDate = supEmpGroup.getStartDate();
            this.endDate = supEmpGroup.getEndDate();
            this.primaryEmployees = new HashMap<>(supEmpGroup.getPrimaryEmployees());
            this.overrideEmployees = new HashMap<>(supEmpGroup.getOverrideEmployees());
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
        Optional<EmployeeSupInfo> supInfoOpt = getAllEmployees().stream()
                .filter(supInfo -> empId == supInfo.getEmpId())
                .filter(employeeSupInfo -> employeeSupInfo.getEffectiveDateRange().contains(date))
                .findAny();
        return supInfoOpt.isPresent();
    }

    /**
     * Filter out any employees in this Supervisor emp group that are not under this supervisor during the
     * given dateRange.
     * @param dateRange Range<LocalDate>
     */
    public void filterActiveEmployeesByDate(Range<LocalDate> dateRange) {
        this.setPrimaryEmployees(this.getPrimaryEmployees().values().stream()
            .filter(supInfo -> isSupInfoInRange(supInfo, dateRange))
            .collect(Collectors.toMap(EmployeeSupInfo::getEmpId, Function.identity())));
        this.setOverrideEmployees(this.getOverrideEmployees().values().stream()
                .filter(supInfo -> isSupInfoInRange(supInfo, dateRange))
                .collect(Collectors.toMap(EmployeeSupInfo::getEmpId, Function.identity())));
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
        return supInfo.getSupEndDate() == null || dateRange.contains(supInfo.getSupEndDate());
    }

    /** --- Functional Getters/Setters --- */

    public Set<Integer> getOverrideSupIds() {
        return supOverrideEmployees.rowKeySet();
    }

    public Set<EmployeeSupInfo> getAllEmployees() {
        Set<EmployeeSupInfo> empSet = new HashSet<>();
        this.primaryEmployees.values().forEach(empSet::add);
        this.overrideEmployees.values().forEach(empSet::add);
        this.supOverrideEmployees.values().forEach(empSet::add);
        return empSet;
    }

    /**
     * Get overridden employees granted by the given supervisor id
     */
    public Map<Integer, EmployeeSupInfo> getSupOverrideEmployees(int supId) {
        return supOverrideEmployees.rowMap().get(supId);
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

    public Map<Integer, EmployeeSupInfo> getPrimaryEmployees() {
        return primaryEmployees;
    }

    public void setPrimaryEmployees(Map<Integer, EmployeeSupInfo> primaryEmployees) {
        this.primaryEmployees = primaryEmployees;
    }

    public Map<Integer, EmployeeSupInfo> getOverrideEmployees() {
        return overrideEmployees;
    }

    public void setOverrideEmployees(Map<Integer, EmployeeSupInfo> overrideEmployees) {
        this.overrideEmployees = overrideEmployees;
    }

    public Table<Integer, Integer, EmployeeSupInfo> getSupOverrideEmployees() {
        return supOverrideEmployees;
    }

    public void setSupOverrideEmployees(Table<Integer, Integer, EmployeeSupInfo> supOverrideEmployees) {
        this.supOverrideEmployees = supOverrideEmployees;
    }
}