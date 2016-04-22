package gov.nysenate.ess.supply.shipment;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;

import java.time.LocalDateTime;
import java.util.EnumSet;

public interface ShipmentService {

    int initializeShipment(Order order);

    Shipment getShipmentById(int shipmentId);

    PaginatedList<Shipment> searchShipments(String issuingEmpId, EnumSet<ShipmentStatus> statuses,
                                            Range<LocalDateTime> dateRange, LimitOffset limoff);

    void addVersionToShipment(ShipmentVersion newVersion, Shipment shipment);

    /**
     * Accepting a shipment returns its status from CANCELED to its previous value.
     * It also sets the shipments order status to APPROVED from REJECTED.
     * @param shipment
     * @param modifiedByEmp
     */
    void acceptShipment(Shipment shipment, Employee modifiedByEmp);
}
