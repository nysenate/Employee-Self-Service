package gov.nysenate.ess.time.model.personnel;

public class SupervisorMissingEmpsEx extends SupervisorException
{
    private static final long serialVersionUID = 792981752519254089L;

    public SupervisorMissingEmpsEx() {
    }

    public SupervisorMissingEmpsEx(String message) {
        super(message);
    }

    public SupervisorMissingEmpsEx(String message, Throwable cause) {
        super(message, cause);
    }
}