package gov.nysenate.ess.core.model.personnel;

public class EmployeeNotFoundEx extends EmployeeException
{
    private static final long serialVersionUID = -2457054505468931008L;

    int empId;

    public EmployeeNotFoundEx(int empId) {
        super("No employee was found with id " + empId);
        this.empId = empId;
    }

    public EmployeeNotFoundEx(String message) {
        super(message);
    }

    public EmployeeNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }

    public int getEmpId() {
        return empId;
    }
}
