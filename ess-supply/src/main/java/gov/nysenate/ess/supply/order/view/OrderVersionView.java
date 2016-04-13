package gov.nysenate.ess.supply.order.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.util.HashSet;
import java.util.Set;

public class OrderVersionView implements ViewObject {

    protected int id;
    protected EmployeeView customer;
    protected LocationView destination;
    protected String status;
    protected Set<LineItemView> lineItems;
    protected EmployeeView modifiedBy;
    protected String note;

    public OrderVersionView() { }

    public OrderVersionView(OrderVersion version) {
        this.id = version.getId();
        this.customer = new EmployeeView(version.getCustomer());
        this.destination = new LocationView(version.getDestination());
        this.status = version.getStatus().toString();
        this.lineItems = new HashSet<>();
        version.getLineItems().forEach(i -> this.lineItems.add(new LineItemView(i)));
        this.modifiedBy = new EmployeeView(version.getModifiedBy());
        this.note = version.getNote().orElse("");
    }

    public OrderVersion toOrderVersion() {
        return new OrderVersion.Builder().withId(id).withCustomer(customer.toEmployee())
                                         .withDestination(destination.toLocation()).withStatus(OrderStatus.valueOf(status))
                                         .withLineItems(getLineItemSet()).withModifiedBy(modifiedBy.toEmployee())
                                         .withNote(note == null ? "" : note).build();
    }

    private Set<LineItem> getLineItemSet() {
        Set<LineItem> lineItemSet = new HashSet<>();
        lineItems.forEach(i -> lineItemSet.add(i.toLineItem()));
        return lineItemSet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmployeeView getCustomer() {
        return customer;
    }

    public void setCustomer(EmployeeView customer) {
        this.customer = customer;
    }

    public LocationView getDestination() {
        return destination;
    }

    public void setDestination(LocationView destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<LineItemView> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<LineItemView> lineItems) {
        this.lineItems = lineItems;
    }

    public EmployeeView getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(EmployeeView modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getViewType() {
        return "order-version-view";
    }
}
