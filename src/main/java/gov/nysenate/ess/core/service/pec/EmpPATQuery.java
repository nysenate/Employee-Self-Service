package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.pec.PATQueryBuilder;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;

import java.util.List;

/**
 * Query object with parameters for {@link Employee} and {@link PersonnelAssignedTask} filtering.
 */
public class EmpPATQuery {

    private EmployeeSearchBuilder empQuery;
    private PATQueryBuilder patQuery;
    private List<EmpTaskSort> sortDirectives;

    public EmpPATQuery(EmployeeSearchBuilder empQuery, PATQueryBuilder patQuery,
                       List<EmpTaskSort> sortDirectives) {
        this.empQuery = empQuery;
        this.patQuery = patQuery;
        this.sortDirectives = sortDirectives;
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

    public List<EmpTaskSort> getSortDirectives() {
        return sortDirectives;
    }

    public void setSortDirectives(List<EmpTaskSort> sortDirectives) {
        this.sortDirectives = sortDirectives;
    }
}
