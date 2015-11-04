package gov.nysenate.ess.web.model.exception;

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
