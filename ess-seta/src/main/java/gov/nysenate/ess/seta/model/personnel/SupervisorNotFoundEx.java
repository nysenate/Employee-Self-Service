package gov.nysenate.ess.seta.model.personnel;

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
