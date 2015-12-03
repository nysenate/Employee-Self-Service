package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;

import java.time.LocalDateTime;
import java.util.Map;

public class Order {

    private final int id;
    private final Employee customer;
    private final LocalDateTime orderDateTime;
    private final Location location;
    private Employee issuingEmployee;
    private LocalDateTime processedDateTime;
    private LocalDateTime completedDateTime;
    private OrderStatus status;
    private Map<Integer, Integer> items;

    public Order(int id, Employee customer, LocalDateTime orderDateTime, Location location) {
        this(id, customer, orderDateTime, location, OrderStatus.PENDING);
    }

    public Order(int id, Employee customer, LocalDateTime orderDateTime, Location location, OrderStatus status) {
        this.id = id;
        this.customer = customer;
        this.orderDateTime = orderDateTime;
        this.location = location;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Employee getCustomer() {
        return customer;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Location getLocation() {
        return location;
    }

    public Employee getIssuingEmployee() {
        return issuingEmployee;
    }

    public void setIssuingEmployee(Employee issuingEmployee) {
        this.issuingEmployee = issuingEmployee;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }

    public void setProcessedDateTime(LocalDateTime processedDateTime) {
        this.processedDateTime = processedDateTime;
    }

    public LocalDateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(LocalDateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Map<Integer, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Integer, Integer> items) {
        this.items = items;
    }
}
