package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data access layer for retrieving employee information.
 */
public interface EmployeeDao extends BaseDao
{
    /**
     * Retrieve an Employee object based on the employee id.
     *
     * @param empId int - Employee id
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeException - EmployeeNotFoundEx if employee with given id was not found.
     */
    Employee getEmployeeById(int empId) throws EmployeeException;

    /**
     * Retrieve an Employee object based on the employee email.
     *
     * @param email String - email
     * @return Employee if found, throws EmployeeNotFoundEx otherwise.
     * @throws EmployeeException - EmployeeNotFoundEx if employee with given email was not found.
     */
    Employee getEmployeeByEmail(String email) throws EmployeeException;

    /**
     * Retrieves a Map of {emp id (Integer) -> Employee} given a collection of employee ids
     * to match against.
     *
     * @param empIds List<Integer> - List of employee ids
     * @return Map - {emp id (Integer) -> Employee} or empty map if no ids could be matched
     */
    Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds);

    /**
     * @return Employee info objects for all past and present employees
     */
    Set<Employee> getAllEmployees();

    /**
     * @return Employee info objects for all currently active employees
     */
    Set<Employee> getActiveEmployees();

    /**
     * Search for employees by their full name.
     * Results are paginated and ordered in alphabetical order.
     *
     * @param term String - search term
     * @param activeOnly boolean - return only active employees if true
     *@param limitOffset {@link LimitOffset} - result window  @return {@link PaginatedList<Employee>}
     */
    PaginatedList<Employee> searchEmployees(String term, boolean activeOnly, LimitOffset limitOffset);

    /**
     * Search for employees based on the given query object.
     *
     * @param employeeSearchBuilder {@link EmployeeSearchBuilder}
     * @param limitOffset {@link LimitOffset}
     * @return {@link PaginatedList}
     */
    PaginatedList<Employee> searchEmployees(EmployeeSearchBuilder employeeSearchBuilder, LimitOffset limitOffset);

    /**
     * @return The ids for all currently active employees
     */
    Set<Integer> getActiveEmployeeIds();

    /**
     * Get raw column data from the employee table.
     * This data will contain the most recent data for all columns that are set by Transactions
     * @see gov.nysenate.ess.core.model.transaction.TransactionCode
     *
     * @param empId int - employee id
     * @return ImmutableMap<String, String>
     */
    Map<String, String> getRawEmployeeColumns(int empId);

    /**
     * @return LocalDateTime - the date/time of the latest employee table update
     */
    LocalDateTime getLastUpdateTime();

    /**
     * Get all employees with fields that were updated after the given date time
     * @param fromDateTime LocalDateTime
     * @return List<Employee>
     */
    List<Employee> getUpdatedEmployees(LocalDateTime fromDateTime);
}
