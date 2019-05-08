package gov.nysenate.ess.core.model.pec.acknowledgment;

/**
 * An exception thrown when an {@link AckDoc} has already been acknowledged.
 */
public class DuplicateAckEx extends RuntimeException {
    private int ackDocid;

    public DuplicateAckEx(int ackDocid) {
        super("The requested document: " + ackDocid + " has already been acknowledged");
        this.ackDocid = ackDocid;
    }

    public int getAckDocid() {
        return ackDocid;
    }

}
