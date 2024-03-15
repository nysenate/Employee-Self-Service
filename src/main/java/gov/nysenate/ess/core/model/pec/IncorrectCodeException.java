package gov.nysenate.ess.core.model.pec;

/**
 * Exception thrown when incorrect verification codes are submitted for a task.
 */
public class IncorrectCodeException extends RuntimeException {
    public IncorrectCodeException(String msg) {
        super(msg);
    }

    public IncorrectCodeException() {
        super("The submitted codes were incorrect for this task");
    }
}
