package gov.nysenate.ess.supply.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.dao.ShipmentDao;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplyShipmentService implements ShipmentService {

    private ShipmentDao shipmentDao;
    private DateTimeFactory dateTimeFactory;

    @Autowired
    public SupplyShipmentService(ShipmentDao shipmentDao, DateTimeFactory dateTimeFactory) {
        this.shipmentDao = shipmentDao;
        this.dateTimeFactory = dateTimeFactory;
    }

    @Override
    public int initializeShipment(Order order, ShipmentVersion version) {
        return shipmentDao.insert(order, version, dateTimeFactory.now());
    }

    @Override
    public void processShipment(Shipment shipment, Employee issuingEmp, Employee modifiedByEmp) {
        Shipment processed = shipment.process(issuingEmp, modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(processed);
    }

    @Override
    public void completeShipment(Shipment shipment, Employee modifiedByEmp) {
        Shipment completed = shipment.complete(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(completed);
    }

    @Override
    public void undoCompletion(Shipment shipment, Employee modifiedByEmp) {
        Shipment undone = shipment.undoCompletion(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(undone);
    }

    @Override
    public void submitToSfms(Shipment shipment, Employee modifiedByEmp) {
        Shipment submitted = shipment.submitToSfms(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(submitted);
    }

    @Override
    public void cancelShipment(Shipment shipment, Employee modifiedByEmp) {
        Shipment canceled = shipment.cancel(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(canceled);
    }

    @Override
    public void updateIssuingEmployee(Shipment shipment, Employee issuingEmp, Employee modifiedByEmp) {
        Shipment updated = shipment.updateIssuingEmployee(issuingEmp, modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(updated);
    }

    @Override
    public Shipment getShipmentById(int shipmentId) {
        return shipmentDao.getById(shipmentId);
    }
}
