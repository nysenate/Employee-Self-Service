package gov.nysenate.ess.supply.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.order.Order;

import java.time.LocalDateTime;
import java.util.Optional;

public class Shipment {

    private final int id;
    private final Order order;
    private final ShipmentHistory shipmentHistory;

    private Shipment(int id, Order order, ShipmentHistory shipmentHistory) {
        this.id = id;
        this.order = order;
        this.shipmentHistory = shipmentHistory;
    }

    /** Static constructors */

    public static Shipment of(int id, Order order, ShipmentHistory shipmentHistory) {
        return new Shipment(id, order, shipmentHistory);
    }

    /** Functional methods */

    public Shipment addVersion(ShipmentVersion newVersion, LocalDateTime modifiedDateTime) {
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Shipment accept(Employee modifiedBy, LocalDateTime modifiedDateTime) {
        // TODO uggly fix this
        ShipmentStatus previousStatus = null;
        for(int i = 0; i < getHistory().getHistory().size() - 1; i++) {
           ShipmentVersion nextVersion = (ShipmentVersion) getHistory().getHistory().values().toArray()[i + 1];
            if(nextVersion.getStatus() == ShipmentStatus.CANCELED) {
                previousStatus = ((ShipmentVersion) getHistory().getHistory().values().toArray()[i]).getStatus();
            }
        }
        ShipmentVersion newVersion = current()
                .setStatus(previousStatus)
                .setModifiedBy(modifiedBy);
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Optional<LocalDateTime> getProcessedDateTime() {
        return shipmentHistory.getProcessedDateTime();
    }

    public Optional<LocalDateTime> getCompletedDateTime() {
        return shipmentHistory.getCompletedDateTime();
    }

    public Optional<LocalDateTime> getApprovedDateTime() {
        return shipmentHistory.getApprovedDateTime();
    }

    public Optional<LocalDateTime> getCanceledDateTime() {
        return shipmentHistory.getCanceledDateTime();
    }

    /** Getters, get values from this Shipment object. */

    public int getId() {
        return this.id;
    }

    public Order getOrder() {
        return this.order;
    }

    public ShipmentHistory getHistory() {
        return shipmentHistory;
    }

    /** Getters, get values from current/most recent entry in shipment history. */

    public ShipmentStatus getStatus() {
        return current().getStatus();
    }

    public Optional<Employee> getIssuingEmployee() {
        return current().getIssuingEmployee();
    }

    public Employee getModifiedBy() {
        return current().getModifiedBy();
    }

    public LocalDateTime getModifiedDateTime() {
        return shipmentHistory.getModifiedDateTime();
    }

    public ShipmentVersion current() {
        return shipmentHistory.current();
    }

    /** Internal Methods */

    private int getNewVersionId() {
        return shipmentHistory.size() + 1;
    }

    @Override
    public String toString() {
        return "Shipment{" +
               "shipmentHistory=" + shipmentHistory +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shipment shipment = (Shipment) o;

        return !(shipmentHistory != null ? !shipmentHistory.equals(shipment.shipmentHistory) : shipment.shipmentHistory != null);

    }

    @Override
    public int hashCode() {
        return shipmentHistory != null ? shipmentHistory.hashCode() : 0;
    }
}
