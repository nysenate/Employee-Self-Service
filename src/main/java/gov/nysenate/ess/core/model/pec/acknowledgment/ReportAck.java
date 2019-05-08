package gov.nysenate.ess.core.model.pec.acknowledgment;

public class ReportAck {

    private AckDoc ackDoc;
    private Acknowledgment ack;

    public ReportAck() {}

    public ReportAck(AckDoc ackDoc,Acknowledgment ack) {
        this.ackDoc = ackDoc;
        this.ack = ack;
    }

    public AckDoc getAckDoc() {
        return ackDoc;
    }

    public void setAckDoc(AckDoc ackDoc) {
        this.ackDoc = ackDoc;
    }

    public Acknowledgment getAck() {
        return ack;
    }

    public void setAck(Acknowledgment ack) {
        this.ack = ack;
    }
}
