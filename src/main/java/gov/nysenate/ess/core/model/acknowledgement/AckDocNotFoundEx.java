package gov.nysenate.ess.core.model.acknowledgement;

/**
 * An exception thrown when an {@link AckDoc} cannot be found for a specific id.
 */
public class AckDocNotFoundEx extends RuntimeException {

    private int ackDocid;

    public AckDocNotFoundEx(int ackDocid) {
        super("Could not find acknowledged document with id: " + ackDocid);
        this.ackDocid = ackDocid;
    }

    public int getAckDocid() {
        return ackDocid;
    }
}
