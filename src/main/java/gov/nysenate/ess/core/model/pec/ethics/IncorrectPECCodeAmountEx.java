package gov.nysenate.ess.core.model.pec.ethics;

public class IncorrectPECCodeAmountEx extends RuntimeException {
    public IncorrectPECCodeAmountEx() {
        super("The incorrect number of codes were submitted for a PEC task");
    }
}
