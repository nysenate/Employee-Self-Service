package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.pec.PATQueryBuilder;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;

/**
 * Query object with parameters for {@link Employee} and {@link PersonnelAssignedTask} filtering.
 */
public class EmpPATQuery {

    private EmployeeSearchBuilder empQuery;
    private PATQueryBuilder patQuery;

    protected EmpPATQuery() {}

    public EmpPATQuery(EmployeeSearchBuilder empQuery, PATQueryBuilder patQuery) {
        this.empQuery = empQuery;
        this.patQuery = patQuery;
    }

    public EmployeeSearchBuilder getEmpQuery() {
        return empQuery;
    }

    public void setEmpQuery(EmployeeSearchBuilder empQuery) {
        this.empQuery = empQuery;
    }

    public PATQueryBuilder getPatQuery() {
        return patQuery;
    }

    public void setPatQuery(PATQueryBuilder patQuery) {
        this.patQuery = patQuery;
    }
}
