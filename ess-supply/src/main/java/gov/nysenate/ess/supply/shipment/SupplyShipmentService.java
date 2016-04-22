package gov.nysenate.ess.supply.shipment;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.dao.ShipmentDao;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
public class SupplyShipmentService implements ShipmentService {

    @Autowired private ShipmentDao shipmentDao;
    @Autowired private DateTimeFactory dateTimeFactory;

    public SupplyShipmentService() {
    }

    public SupplyShipmentService(ShipmentDao shipmentDao, DateTimeFactory dateTimeFactory) {
        this.shipmentDao = shipmentDao;
        this.dateTimeFactory = dateTimeFactory;
    }

    @Override
    public int initializeShipment(Order order) {
        ShipmentVersion version = new ShipmentVersion.Builder()
                .withStatus(ShipmentStatus.PENDING).withModifiedBy(order.getCustomer()).build();
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
    public void approveShipment(Shipment shipment, Employee modifiedByEmp) {
        Shipment submitted = shipment.approve(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(submitted);
    }

    @Override
    public void cancelShipment(Shipment shipment, Employee modifiedByEmp) {
        Shipment canceled = shipment.cancel(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(canceled);
    }

    @Override
    public void acceptShipment(Shipment shipment, Employee modifiedByEmp) {
        Shipment accepted = shipment.accept(modifiedByEmp, dateTimeFactory.now());
        shipmentDao.save(accepted);
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

    @Override
    public PaginatedList<Shipment> searchShipments(String issuingEmpId, EnumSet<ShipmentStatus> statuses,
                                                   Range<LocalDateTime> dateRange, LimitOffset limoff) {
        return shipmentDao.searchShipments(issuingEmpId, statuses, dateRange, limoff);
    }

    @Override
    public void addVersionToShipment(ShipmentVersion newVersion, Shipment shipment) {
        Shipment updated = shipment.addVersion(newVersion, dateTimeFactory.now());
        shipmentDao.save(updated);
    }
}
