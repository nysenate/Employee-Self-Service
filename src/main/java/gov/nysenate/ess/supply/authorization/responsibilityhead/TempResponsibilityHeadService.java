package gov.nysenate.ess.supply.authorization.responsibilityhead;

import gov.nysenate.ess.core.dao.personnel.rch.SqlResponsibilityHeadDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetches and converts {@link TempResponsibilityHead} into {@link ResponsibilityHead}.
 */
@Service
public class TempResponsibilityHeadService {

    private SqlResponsibilityHeadDao rchDao;
    private SqlTempResponsibilityHeadDao tempRchDao;

    @Autowired
    public TempResponsibilityHeadService(SqlResponsibilityHeadDao rchDao, SqlTempResponsibilityHeadDao tempRchDao) {
        this.rchDao = rchDao;
        this.tempRchDao = tempRchDao;
    }

    /**
     * Creates and returns a {@link ResponsibilityHead} for each {@link TempResponsibilityHead}
     * currently effective for an employee.
     *
     * @param employee The employee to get temp RCH's for.
     * @return A list of an employees temporary {@link ResponsibilityHead}'s effective now.
     */
    public List<ResponsibilityHead> tempRchForEmp(Employee employee) {
        return tempRchDao.tempRchForEmp(employee.getEmployeeId()).stream()
                .map(t -> rchDao.rchForCode(t.getResponsibilityHeadCode()))
                .collect(Collectors.toList());
    }
}
