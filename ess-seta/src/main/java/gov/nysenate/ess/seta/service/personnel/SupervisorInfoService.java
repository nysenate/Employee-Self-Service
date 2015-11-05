package gov.nysenate.ess.seta.service.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.seta.model.personnel.SupervisorOverride;
import gov.nysenate.ess.seta.model.personnel.SupervisorException;
import gov.nysenate.ess.seta.model.personnel.SupervisorChain;
import gov.nysenate.ess.seta.model.personnel.SupervisorEmpGroup;

import java.time.LocalDate;
import java.util.List;

public interface SupervisorInfoService
{
    /**
     * Determines if the given employee is a supervisor at any point during the given date range.
     *
     * @param supId int
     * @param dateRange Range<LocalDate>
     * @return boolean - true if employee was a supervisor during this range.
     */
    boolean isSupervisorDuring(int supId, Range<LocalDate> dateRange);

    default boolean isSupervisorDuring(int supId) {
        LocalDate now = LocalDate.now();
        return isSupervisorDuring(supId, Range.closed(DateUtils.firstDayOfPreviousYear(now), now));
    }

    /**
     * Retrieve the effective T&A supervisor id for the given employee id during the supplied date.
     *
     * @param empId int - Employee id
     * @param date LocalDate - Point in time to get the supervisor for
     * @return int - Supervisor id
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    int getSupervisorIdForEmp(int empId, LocalDate date) throws SupervisorException;

    /**
     * Returns the supervisor employee group for the given employee id, filtered by the given date range.
     * This service layer implementation should make an effort to cache this data so it's preferable to use
     * this over the similar dao method when performance is required.
     *
     * @param supId int
     * @param dateRange Range<LocalDate>
     * @return SupervisorEmpGroup
     * @throws SupervisorException - will be thrown if the employee was never a supervisor.
     */
    SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException;

    default SupervisorEmpGroup getSupervisorEmpGroup(int supId) throws SupervisorException {
        LocalDate now = LocalDate.now();
        return getSupervisorEmpGroup(supId, Range.closed(DateUtils.firstDayOfPreviousYear(now), now));
    }

    /**
     * Returns a chain of supervisors that are above the given supervisor on the given date.
     *
     * @param supId int
     * @param activeDate LocalDate
     * @param maxChainLength int - the maximum number of parent supervisors to fetch.
     * @return SupervisorChain
     * @throws SupervisorException - will be thrown if the employee was never a supervisor.
     */
    SupervisorChain getSupervisorChain(int supId, LocalDate activeDate, int maxChainLength) throws SupervisorException;

    /**
     * Retrieves a list of active supervisor overrides that have been granted to the given supervisor.
     * @param supId int - Supervisor id
     * @return List<SupervisorOverride>
     * @throws SupervisorException
     */
    List<SupervisorOverride> getSupervisorOverrides(int supId) throws SupervisorException;

    /**
     * Retrieves a list of supervisors that have been granted overrides by the given supervisor.
     * @param supId int - Supervisor id
     * @return List<SupervisorOverride>
     * @throws SupervisorException
     */
    List<SupervisorOverride> getSupervisorGrants(int supId) throws SupervisorException;

    /**
     *
     * @param override SupervisorOverride
     * @throws SupervisorException - SupervisorNotFoundEx if either supervisor could not be found
     *                               SupervisorNotInChainEx if 'ovrSupId' is not in 'supId's' chain
     */
    void updateSupervisorOverride(SupervisorOverride override) throws SupervisorException;
}