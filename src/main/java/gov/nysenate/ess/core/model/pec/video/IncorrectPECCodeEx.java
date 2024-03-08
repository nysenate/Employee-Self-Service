package gov.nysenate.ess.core.model.pec.video;

/**
 * Exception thrown when incorrect video codes are submitted for a {@link VideoTask}
 */
public class IncorrectPECCodeEx extends RuntimeException {
    public IncorrectPECCodeEx() {
        super("The submitted codes were incorrect for a PEC task");
    }
}
