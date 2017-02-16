package gov.nysenate.ess.core.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.personnel.Person;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface EmployeeInfoService
{
    /**
     * Retrieves an employee based on employee id. The implementation of this method should cache
     * the employees for faster retrieval than from the dao layer.
     * @param empId int
     * @return Employee
     * @exception EmployeeNotFoundEx - If employee with given empId was not found.
     */
    Employee getEmployee(int empId) throws EmployeeNotFoundEx;

    /**
     * Get a snapshot of an employee at a specific date.
     * Gets only the fields declared in the Employee class for the specified date
     * Uses the currently up to date values for the Person fields
     * @see Person
     * @see Employee
     * @param empId int
     * @param effectiveDate LocalDate
     * @return Employee
     * @throws EmployeeNotFoundEx - If an employee with the given empId was not found.
     */
    Employee getEmployee(int empId, LocalDate effectiveDate) throws EmployeeNotFoundEx;

    /**
     * Delegates {@link #getEmployee(int)}
     * Takes multiple employee ids and returns {@link Employee} objects for each
     * @param empIds Set<Integer> employee Ids
     * @return {@link Map<Integer, Employee>}
     * @throws EmployeeNotFoundEx
     */
    default Map<Integer, Employee> getEmployees(Set<Integer> empIds) throws EmployeeNotFoundEx {
        return empIds.stream()
            .map(this::getEmployee)
            .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
    }

    /**
     * Get a date range set encapsulating the employee's dates of active service
     * @param empId Integer - employee id
     * @return RangeSet<LocalDate>
     */
    RangeSet<LocalDate> getEmployeeActiveDatesService(int empId);

    /**
     * Get a list of years that an employee was active
     * @param empId Integer - employee id
     * @param fiscalYears boolean - will return active fiscal years if set true
     * @return List<Integer> - list of years
     */
    List<Integer> getEmployeeActiveYearsService(int empId, boolean fiscalYears);

    /**
     * Overload of {@link #getEmployeeActiveYearsService(int, boolean)}
     * that uses standard, as opposed to fiscal years by default
     * @param empId Integer - employee id
     * @return List<Integer> - list of years
     */
    default List<Integer> getEmployeeActiveYearsService(int empId) {
        return getEmployeeActiveYearsService(empId, false);
    }

    /**
     *  Get a set of ids for all currently active employees
     * @return NavigableSet<Integer>
     */
    Set<Integer> getActiveEmpIds();

}