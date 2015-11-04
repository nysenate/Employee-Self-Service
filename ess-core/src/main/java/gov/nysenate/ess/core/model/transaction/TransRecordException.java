package gov.nysenate.ess.core.model.transaction;

public class TransRecordException extends Exception
{
    public TransRecordException() {
        super();
    }

    public TransRecordException(String message) {
        super(message);
    }

    public TransRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
