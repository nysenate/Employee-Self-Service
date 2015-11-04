package gov.nysenate.ess.core.service.personnel;

import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.Person;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    default Map<Integer, Employee> getEmployees(Set<Integer> empIds) throws EmployeeNotFoundEx {
        return empIds.stream()
            .map(this::getEmployee)
            .collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
    }

    RangeSet<LocalDate> getEmployeeActiveDatesService(int empId);

    default List<Integer> getEmployeeActiveYearsService(int empId) {
        RangeSet<LocalDate> rangeSet = getEmployeeActiveDatesService(empId);
        return rangeSet.asRanges().stream().flatMapToInt(r -> {
            if (r.hasLowerBound()) {
                int upperBound = (r.hasUpperBound()) ? DateUtils.endOfDateRange(r).getYear() : LocalDate.now().getYear();
                return IntStream.rangeClosed(r.lowerEndpoint().getYear(), upperBound);
            }
            return IntStream.empty();
        }).boxed().distinct().collect(Collectors.toList());
    }
}