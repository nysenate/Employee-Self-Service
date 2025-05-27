package gov.nysenate.ess.time.dao.payroll;

import gov.nysenate.ess.time.model.payroll.MiscLeaveGrant;

import java.util.List;

public interface MiscLeaveDao {

    /**
     * Get a list of misc leave grants that are active for the given employee
     * @param empId int - employee id
     * @return List<MiscLeaveGrant>
     */
    List<MiscLeaveGrant> getMiscLeaveGrants(int empId);
}
