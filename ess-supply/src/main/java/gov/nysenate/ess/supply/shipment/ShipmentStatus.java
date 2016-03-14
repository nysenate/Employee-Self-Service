package gov.nysenate.ess.supply.shipment;

/**
 * The statuses of a shipment.
 * Each shipment starts with a <code>PENDING</code> status, and goes through
 * <code>PROCESSING</code>, <code>COMPLETED</code>, and <code>APPROVED</code> in that order.
 *
 * A shipment will have a status of <code>CANCELED</code> if the shipments {@link gov.nysenate.ess.supply.order.Order}
 * gets rejected.
 *
 * All shipments should end up either <code>APPROVED</code> or <code>CANCELED</code>
 */
public enum ShipmentStatus {
    /** Shipment is usually canceled if its order is rejected. */
    CANCELED,
    /** Default status. Shipment is not being worked on yet. */
    PENDING,
    /** Items in order are being gathered and/or currently out being shipped. */
    PROCESSING,
    /** Shipment has been completed but not yet approved into SFMS. */
    COMPLETED,
    /** Approved and saved into SFMS. */
    APPROVED
}
