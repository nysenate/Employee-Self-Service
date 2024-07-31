package gov.nysenate.ess.travel.department;

public class DepartmentNotFoundEx extends Exception {

    int empId;

    public DepartmentNotFoundEx(int empId) {
        super("No department was found for employee with empId: " + empId);
        this.empId = empId;
    }

    public DepartmentNotFoundEx(String message) {
        super(message);
    }

    public DepartmentNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }

    public int getEmpId() {
        return empId;
    }
}
