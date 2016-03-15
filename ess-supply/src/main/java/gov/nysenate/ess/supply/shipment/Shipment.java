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

    public Shipment process(Employee issuingEmployee, Employee modifiedBy, LocalDateTime modifiedDateTime) {
        ShipmentVersion newVersion = current()
                .setId(getNewVersionId())
                .setStatus(ShipmentStatus.PROCESSING)
                .setIssuingEmployee(issuingEmployee)
                .setModifiedBy(modifiedBy);
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Shipment complete(Employee modifiedBy, LocalDateTime modifiedDateTime) {
        ShipmentVersion newVersion = current()
                .setId(getNewVersionId())
                .setStatus(ShipmentStatus.COMPLETED)
                .setModifiedBy(modifiedBy);
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Shipment undoCompletion(Employee modifiedBy, LocalDateTime modifiedDateTime) {
        ShipmentVersion newVersion = current()
                .setId(getNewVersionId())
                .setStatus(ShipmentStatus.PROCESSING)
                .setModifiedBy(modifiedBy);
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Shipment submitToSfms(Employee modifiedBy, LocalDateTime modifiedDateTime) {
        ShipmentVersion newVersion = current()
                .setId(getNewVersionId())
                .setStatus(ShipmentStatus.APPROVED)
                .setModifiedBy(modifiedBy);
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Shipment cancel(Employee modifiedBy, LocalDateTime modifiedDateTime) {
        ShipmentVersion newVersion = current()
                .setId(getNewVersionId())
                .setStatus(ShipmentStatus.CANCELED)
                .setModifiedBy(modifiedBy);
        return Shipment.of(this.id, this.order, shipmentHistory.addVersion(modifiedDateTime, newVersion));
    }

    public Shipment updateIssuingEmployee(Employee issuingEmployee, Employee modifiedBy, LocalDateTime modifiedDateTime) {
        ShipmentVersion newVersion = current()
                .setId(getNewVersionId())
                .setIssuingEmployee(issuingEmployee)
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

    public ShipmentHistory getHistory() {
        return shipmentHistory;
    }

    /** Getters, get values from current/most recent entry in shipment history. */

    public ShipmentStatus getStatus() {
        return current().getStatus();
    }

    public Employee getIssuingEmployee() {
        return current().getIssuingEmployee();
    }

    public Employee getModifiedBy() {
        return current().getModifiedBy();
    }

    /** Internal Methods */

    private ShipmentVersion current() {
        return shipmentHistory.current();
    }

    private int getNewVersionId() {
        return shipmentHistory.size() + 1;
    }

    @Override
    public String toString() {
        return "Shipment{" +
               "id=" + id +
               ", order=" + order +
               ", shipmentHistory=" + shipmentHistory +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        if (id != shipment.id) return false;
        if (order != null ? !order.equals(shipment.order) : shipment.order != null) return false;
        return !(shipmentHistory != null ? !shipmentHistory.equals(shipment.shipmentHistory) : shipment.shipmentHistory != null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (shipmentHistory != null ? shipmentHistory.hashCode() : 0);
        return result;
    }
}
