package gov.nysenate.ess.supply.shipment.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;

public class ShipmentVersionView implements ViewObject {

    protected int id;
    protected String status;
    protected EmployeeView issuer;
    protected EmployeeView modifiedBy;

    public ShipmentVersionView() { }

    public ShipmentVersionView(ShipmentVersion version) {
        this.id = version.getId();
        this.status = version.getStatus().toString();
        this.issuer = version.getIssuingEmployee().isPresent() ? new EmployeeView(version.getIssuingEmployee().get()) : null;
        this.modifiedBy = new EmployeeView(version.getModifiedBy());
    }

    public ShipmentVersion toShipmentVersion() {
        return new ShipmentVersion.Builder().withId(id)
                                            .withStatus(ShipmentStatus.valueOf(status))
                                            .withIssuingEmployee(issuer.toEmployee())
                                            .withModifiedBy(modifiedBy.toEmployee())
                                            .build();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public EmployeeView getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(EmployeeView modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String getViewType() {
        return "shipment-version-view";
    }
}
