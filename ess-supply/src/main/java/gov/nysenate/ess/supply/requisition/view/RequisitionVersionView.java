package gov.nysenate.ess.supply.requisition.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import java.util.Set;
import java.util.stream.Collectors;

public class RequisitionVersionView implements ViewObject {

    protected int id;
    protected EmployeeView customer;
    protected LocationView destination;
    protected Set<LineItemView> lineItems;
    protected String status;
    protected EmployeeView issuer;
    protected EmployeeView createdBy;
    protected String note;

    public RequisitionVersionView() {}

    public RequisitionVersionView(RequisitionVersion version) {
        this.id = version.getId();
        this.customer = new EmployeeView(version.getCustomer());
        this.destination = new LocationView(version.getDestination());
        this.lineItems = version.getLineItems().stream().map(LineItemView::new).collect(Collectors.toSet());
        this.status = version.getStatus().toString();
        this.issuer = version.getIssuer().map(EmployeeView::new).orElse(null);
        this.createdBy = new EmployeeView(version.getCreatedBy());
        this.note = version.getNote().orElse(null);
    }

    public RequisitionVersion toRequisitionVersion() {
        return new RequisitionVersion.Builder().withId(id)
                                               .withCustomer(customer.toEmployee())
                                               .withDestination(destination.toLocation())
                                               .withLineItems(lineItems.stream().map(LineItemView::toLineItem).collect(Collectors.toSet()))
                                               .withStatus(RequisitionStatus.valueOf(status))
                                               .withIssuer(issuer == null ? null : issuer.toEmployee())
                                               .withCreatedBy(createdBy.toEmployee())
                                               .withNote(note)
                                               .build();
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

    public Set<LineItemView> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<LineItemView> lineItems) {
        this.lineItems = lineItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeView getIssuer() {
        return issuer;
    }

    public void setIssuer(EmployeeView issuer) {
        this.issuer = issuer;
    }

    public EmployeeView getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(EmployeeView createdBy) {
        this.createdBy = createdBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getViewType() {
        return "requisition-version";
    }
}
