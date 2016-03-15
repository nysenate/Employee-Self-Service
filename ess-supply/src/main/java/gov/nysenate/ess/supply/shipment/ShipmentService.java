package gov.nysenate.ess.supply.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.order.Order;

public interface ShipmentService {

    int initializeShipment(Order order, ShipmentVersion version);

    void processShipment(Shipment shipment, Employee issuingEmp, Employee modifiedByEmp);

    void completeShipment(Shipment shipment, Employee modifiedByEmp);

    void undoCompletion(Shipment shipment, Employee modifiedByEmp);

    void submitToSfms(Shipment shipment, Employee modifiedByEmp);

    void cancelShipment(Shipment shipment, Employee modifiedByEmp);

    void updateIssuingEmployee(Shipment shipment, Employee issuingEmp, Employee modifiedByEmp);

    Shipment getShipmentById(int shipmentId);
}
