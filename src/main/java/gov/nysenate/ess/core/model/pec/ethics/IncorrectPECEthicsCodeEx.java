package gov.nysenate.ess.core.model.pec.ethics;

public class IncorrectPECEthicsCodeEx extends RuntimeException {
    public IncorrectPECEthicsCodeEx() {
        super("The submitted codes were incorrect for an Ethics task");
    }
}
