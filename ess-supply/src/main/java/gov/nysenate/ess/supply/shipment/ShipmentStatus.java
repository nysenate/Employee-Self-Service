package gov.nysenate.ess.supply.shipment;

/**
 * The statuses of a shipment.
 * Each shipment starts with a <code>PENDING</code> status, and goes through
 * <code>PROCESSING</code>, <code>COMPLETED</code>, and <code>APPROVED</code> in that order.
 * This order is reflected in their rank values.
 *
 * A shipment will have a status of <code>CANCELED</code> if the shipments {@link gov.nysenate.ess.supply.order.Order}
 * gets rejected.
 *
 * All shipments should end up either <code>APPROVED</code> or <code>CANCELED</code>
 */
public enum ShipmentStatus {
    /** Shipment is usually canceled if its order is rejected. */
    CANCELED(0),
    /** Default status. Shipment is not being worked on yet. */
    PENDING(1),
    /** Items in order are being gathered and/or currently out being shipped. */
    PROCESSING(2),
    /** Shipment has been completed but not yet approved into SFMS. */
    COMPLETED(3),
    /** Approved and saved into SFMS. */
    APPROVED(4);

    private int rank;

    ShipmentStatus(int priority) {
        this.rank = priority;
    }

    public int getRank() {
        return this.rank;
    }
}
