package gov.nysenate.ess.core.model.transaction;

public class TransRecordNotFoundEx extends TransRecordException
{
    public TransRecordNotFoundEx() {
        super();
    }

    public TransRecordNotFoundEx(String message) {
        super(message);
    }

    public TransRecordNotFoundEx(String message, Throwable cause) {
        super(message, cause);
    }
}
