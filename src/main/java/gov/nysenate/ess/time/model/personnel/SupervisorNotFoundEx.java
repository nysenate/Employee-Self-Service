package gov.nysenate.ess.time.model.personnel;

public class SupervisorNotFoundEx extends SupervisorException
{
    public SupervisorNotFoundEx() {}

    public SupervisorNotFoundEx(String message) {
        super(message);
    }

    public SupervisorNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}
