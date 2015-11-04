package gov.nysenate.ess.core.model.transaction;

public class TransactionHistoryMissingEx extends RuntimeException
{
    int empId;

    public TransactionHistoryMissingEx(int empId) {
        super("The transaction history for employee with id: " + empId + " is missing");
        this.empId = empId;
    }

    public TransactionHistoryMissingEx(int empId, String message) {
        super(message);
        this.empId = empId;
    }

    public int getEmpId() {
        return empId;
    }
}
