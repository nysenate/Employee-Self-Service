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

    void processShipment(Shipment shipment, Employee issuingEmp, Employee modifiedByEmp);

    void completeShipment(Shipment shipment, Employee modifiedByEmp);

    void undoCompletion(Shipment shipment, Employee modifiedByEmp);

    /**
     * When a shipment is approved it is no longer editable and its
     * information is saved into the SFMS database.
     * Approvals occur as the last step in supply's workflow.
     * @param shipment
     * @param modifiedByEmp
     */
    void approveShipment(Shipment shipment, Employee modifiedByEmp);

    void cancelShipment(Shipment shipment, Employee modifiedByEmp);

    void updateIssuingEmployee(Shipment shipment, Employee issuingEmp, Employee modifiedByEmp);

    Shipment getShipmentById(int shipmentId);

    PaginatedList<Shipment> searchShipments(String issuingEmpId, EnumSet<ShipmentStatus> statuses,
                                            Range<LocalDateTime> dateRange, LimitOffset limoff);
}
