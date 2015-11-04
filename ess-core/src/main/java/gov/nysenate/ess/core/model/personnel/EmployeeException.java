package gov.nysenate.ess.core.model.personnel;

public class EmployeeException extends RuntimeException
{
    private static final long serialVersionUID = 6558244095009074935L;

    public EmployeeException() {}

    public EmployeeException(String message) {
        super(message);
    }

    public EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
