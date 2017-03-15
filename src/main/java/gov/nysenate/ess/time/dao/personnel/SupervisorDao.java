package gov.nysenate.ess.time.dao.personnel;

import gov.nysenate.ess.core.model.transaction.TransactionInfo;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.personnel.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data access layer for retrieving T&A supervisor info as well as setting overrides.
 */
public interface SupervisorDao extends BaseDao
{

    /**
     * Indicates whether the given empId was ever a T&A supervisor
     * @param empId int - employee id
     * @return boolean - true iff 'empId' had subordinates at any point during their time at the senate.
     */
    boolean isSupervisor(int empId);

    /**
     * Retrieves any supervisor ids that are designated to be included or excluded from the
     * given employee's supervisor chain.
     *
     * @param empId int
     * @return SupervisorChainAlteration
     */
    SupervisorChainAlteration getSupervisorChainAlterations(int empId);

    /**
     * Retrieves the collection of primary employees were ever managed by the given supervisor
     *
     * @param supId int - Supervisor id
     * @return SupervisorEmpGroup if successful, throws SupervisorException otherwise
     * @throws SupervisorException - SupervisorNotFoundEx if the supervisor could not be found
     */
    PrimarySupEmpGroup getPrimarySupEmpGroup(int supId) throws SupervisorException;

    /**
     * Retrieves a list of active supervisor and employee overrides for the given supervisor.
     * @param supId int - Supervisor Id
     * @return {@link List<SupervisorOverride>}
     * @throws SupervisorException
     */
    List<SupervisorOverride> getAllOverrides(int supId) throws SupervisorException;

    /**
     * Retrieves a list of active supervisor overrides for the given supervisor.
     * @param supId int - Supervisor id
     * @return List<SupervisorOverride>
     * @throws SupervisorException
     */
    List<SupervisorOverride> getSupervisorOverrides(int supId) throws SupervisorException;

    /**
     * Retrieves a list of active supervisor grants for the given supervisor.
     * @param supId int - Supervisor id
     * @return List<SupervisorOverride>
     * @throws SupervisorException
     */
    List<SupervisorOverride> getSupervisorGrants(int supId) throws SupervisorException;

    /**
     * Sets an override so that 'ovrSupId' can have access to the primary employees of 'supId' during the
     * given date range.
     *
     * @param granterSupId int - The supervisor granting the override.
     * @param granteeSupId int - The supervisor receiving the override.
     * @param active boolean - Indicates if this grant is active.
     * @param startDate LocalDate - set to null for no start date.
     * @param endDate LocalDate - set to null for no end date.
     */
    void setSupervisorOverride(int granterSupId, int granteeSupId, boolean active, LocalDate startDate, LocalDate endDate);

    /**
     * Get a list of supervisor-relevant (APP, RTP, EMP, SUP) transactions
     * that were updated since the given date time
     *
     * @param fromDateTime LocalDateTime - Gets transactions after this date
     * @return List<TransactionInfo>
     */
    List<TransactionInfo> getSupTransChanges(LocalDateTime fromDateTime);

    /**
     * Return all supervisor overrides updated since the given date time
     *
     * @param fromDateTime LocalDateTime - Looks for changes after this date
     * @return List<Integer>
     */
    List<SupervisorOverride> getSupOverrideChanges(LocalDateTime fromDateTime);

    /**
     * @return LocalDateTime - The date and time of the latest sup-related transaction or sup override update
     */
    LocalDateTime getLastSupUpdateDate();
}