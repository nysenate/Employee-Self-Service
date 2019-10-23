package gov.nysenate.ess.core.model.pec.video;

/**
 * Exception thrown when incorrect video codes are submitted for a {@link VideoTask}
 */
public class IncorrectPECVideoCodeEx extends RuntimeException {
    public IncorrectPECVideoCodeEx() {
        super("The submitted codes were incorrect for a PEC Video");
    }
}
