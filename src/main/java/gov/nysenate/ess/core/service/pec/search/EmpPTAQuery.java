package gov.nysenate.ess.core.service.pec.search;

import gov.nysenate.ess.core.dao.pec.assignment.PTAQueryBuilder;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;

import java.util.List;

/**
 * Query object with parameters for {@link Employee} and {@link PersonnelTaskAssignment} filtering.
 */
public class EmpPTAQuery {

    private EmployeeSearchBuilder empQuery;
    private PTAQueryBuilder patQuery;
    private List<EmpTaskSort> sortDirectives;

    public EmpPTAQuery(EmployeeSearchBuilder empQuery, PTAQueryBuilder patQuery,
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

    public PTAQueryBuilder getPatQuery() {
        return patQuery;
    }

    public void setPatQuery(PTAQueryBuilder patQuery) {
        this.patQuery = patQuery;
    }

    public List<EmpTaskSort> getSortDirectives() {
        return sortDirectives;
    }

    public void setSortDirectives(List<EmpTaskSort> sortDirectives) {
        this.sortDirectives = sortDirectives;
    }
}
