package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.pec.PATQueryBuilder;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;

/**
 * Query object with parameters for {@link Employee} and {@link PersonnelAssignedTask} filtering.
 */
public class EmpPATQuery {

    private EmployeeSearchBuilder employeeSearchBuilder;
    private PATQueryBuilder patQueryBuilder;

    public EmpPATQuery(EmployeeSearchBuilder employeeSearchBuilder, PATQueryBuilder patQueryBuilder) {
        this.employeeSearchBuilder = employeeSearchBuilder;
        this.patQueryBuilder = patQueryBuilder;
    }

    public EmployeeSearchBuilder getEmployeeSearchBuilder() {
        return employeeSearchBuilder;
    }

    public void setEmployeeSearchBuilder(EmployeeSearchBuilder employeeSearchBuilder) {
        this.employeeSearchBuilder = employeeSearchBuilder;
    }

    public PATQueryBuilder getPatQueryBuilder() {
        return patQueryBuilder;
    }

    public void setPatQueryBuilder(PATQueryBuilder patQueryBuilder) {
        this.patQueryBuilder = patQueryBuilder;
    }
}
