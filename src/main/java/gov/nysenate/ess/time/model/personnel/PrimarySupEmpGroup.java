package gov.nysenate.ess.time.model.personnel;

import com.google.common.collect.*;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrimarySupEmpGroup {
    /** The employee id of the supervisor this group is associated with. */
    protected int supervisorId;

    /** Employees were under this supervisor on/after this date.
     * Used to restrict scope of emp group.*/
    protected LocalDate startDate = DateUtils.LONG_AGO;

    /** Employees were under this supervisor before this date. */
    protected LocalDate endDate = DateUtils.THE_FUTURE;

    /** Primary employees that directly assigned to this supervisor.
     *  Mapping of empId -> EmployeeSupInfo */
    protected Multimap<Integer, EmployeeSupInfo> primaryEmployees = HashMultimap.create();

    /** --- Constructors --- */

    public PrimarySupEmpGroup(PrimarySupEmpGroup supEmpGroup) {
        if (supEmpGroup != null) {
            this.supervisorId = supEmpGroup.supervisorId;
            this.startDate = supEmpGroup.startDate;
            this.endDate = supEmpGroup.endDate;
            this.primaryEmployees = HashMultimap.create(supEmpGroup.primaryEmployees);
        }
    }

    public PrimarySupEmpGroup(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    /* --- Methods --- */

    /**
     * @return true if any employees are stored, false otherwise.
     */
    public boolean hasEmployees() {
        return primaryEmployees != null && !primaryEmployees.isEmpty();
    }

    public boolean hasEmployeeAtDate(int empId, LocalDate date) {
        return hasEmployeeDuringRange(empId, Range.singleton(date));
    }

    public boolean hasEmployeeDuringRange(int empId, Range<LocalDate> dateRange) {
        return anySupInfoInRange(primaryEmployees.get(empId), dateRange);
    }

    /**
     * Change the active dates of this emp group, filtering out any {@link SupervisorEmpGroup}s
     * that do are not intersecting with the new new date range
     * @param dateRange Range<LocalDate>
     */
    public void setActiveDates(Range<LocalDate> dateRange) {

        Range<LocalDate> currentDateRange = Range.closedOpen(getStartDate(), getEndDate());
        // Do nothing if the new date range covers the existing one
        if (dateRange.encloses(currentDateRange)) {
            return;
        }
        // Set query dates to reflect new range
        LocalDate newStartDate = DateUtils.startOfDateRange(dateRange);
        LocalDate newEndDate = DateUtils.endOfDateRange(dateRange).plusDays(1);
        if (newStartDate.isAfter(getStartDate())) {
            this.startDate = newStartDate;
        }
        if (newEndDate.isBefore(getEndDate())) {
            this.endDate = newEndDate;
        }

        filterEmpInfos();
    }

    /**
     * Filters out any {@link EmployeeSupInfo} that do not intersect with the active dates of this emp group
     */
    protected void filterEmpInfos() {
        HashMultimap<Integer, EmployeeSupInfo> newPrimaryEmpMap = HashMultimap.create();
        filterEmployeeSupInfos(primaryEmployees.values())
                .forEach(esi -> newPrimaryEmpMap.put(esi.getEmpId(), esi));
        this.setPrimaryEmployees(newPrimaryEmpMap);
    }

    /**
     * Determines if an EmployeeSupInfo is contained within the given date range.
     * @param supInfo EmployeeSupInfo
     * @param dateRange Range<LocalDate>
     * @return boolean
     */
    protected boolean isSupInfoInRange(EmployeeSupInfo supInfo, Range<LocalDate> dateRange) {
        return RangeUtils.intersects(dateRange, supInfo.getEffectiveDateRange());
    }

    protected boolean anySupInfoInRange(Collection<EmployeeSupInfo> supInfos, Range<LocalDate> dateRange) {
        return Optional.ofNullable(supInfos)
                .orElse(Collections.emptyList())
                .stream()
                .anyMatch(supInfo -> isSupInfoInRange(supInfo, dateRange));
    }

    /**
     * Restrict and filter the given {@link EmployeeSupInfo}s by the given date range
     * @param empSupInfos {@link Collection<EmployeeSupInfo>}
     * @return {@link List<EmployeeSupInfo>}
     */
    protected List<EmployeeSupInfo> filterEmployeeSupInfos(Collection<EmployeeSupInfo> empSupInfos) {
        return empSupInfos.stream()
                .filter(esi -> isSupInfoInRange(esi, getActiveDateRange()))
                .map(esi -> esi.restrictDates(getActiveDateRange()))
                .filter(esi -> isSupInfoInRange(esi, getActiveDateRange()))
                .collect(Collectors.toList());
    }

    /* --- Functional Getters/Setters --- */

    public ImmutableMultimap<Integer, EmployeeSupInfo> getPrimaryEmployees() {
        return ImmutableMultimap.copyOf(primaryEmployees);
    }

    public ImmutableSet<EmployeeSupInfo> getPrimaryEmpSupInfos() {
        return ImmutableSet.copyOf(primaryEmployees.values());
    }

    public void setPrimaryEmployees(Multimap<Integer, EmployeeSupInfo> primaryEmployees) {
        this.primaryEmployees = HashMultimap.create(primaryEmployees);
    }

    public Range<LocalDate> getActiveDateRange() {
        return Range.closedOpen(startDate, endDate);
    }

    /** --- Basic Getters/Setters --- */

    public int getSupervisorId() {
        return supervisorId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
}
