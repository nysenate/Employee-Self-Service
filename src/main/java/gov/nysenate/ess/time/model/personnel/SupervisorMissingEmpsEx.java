package gov.nysenate.ess.time.model.personnel;

import java.time.LocalDate;

public class SupervisorMissingEmpsEx extends SupervisorException
{
    private static final long serialVersionUID = 792981752519254089L;

    public SupervisorMissingEmpsEx(int empId) {
        super("Employee " + empId + " has no employee associations");
    }

    public SupervisorMissingEmpsEx(int empId, LocalDate endDate) {
        super("Employee " + empId + " has no employee associations before " + endDate);
    }
}