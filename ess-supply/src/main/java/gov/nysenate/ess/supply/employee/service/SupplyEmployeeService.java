package gov.nysenate.ess.supply.employee.service;

import com.google.common.collect.ImmutableCollection;
import gov.nysenate.ess.core.model.personnel.Employee;

public interface SupplyEmployeeService {

    /**
     * @return A collection of all supply {@link Employee}.
     */
    ImmutableCollection<Employee> getSupplyEmployees();
}
