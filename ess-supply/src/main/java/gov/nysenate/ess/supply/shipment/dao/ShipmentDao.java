package gov.nysenate.ess.supply.shipment.dao;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;

import java.time.LocalDateTime;
import java.util.EnumSet;

public interface ShipmentDao {

    int insert(Order order, ShipmentVersion version, LocalDateTime modifiedDateTime);

    void save(Shipment shipment);

    Shipment getById(int shipmentId);

    PaginatedList<Shipment> getShipments(String issuingEmpId, EnumSet<ShipmentStatus> statuses,
                                         Range<LocalDateTime> dateRange, LimitOffset limoff);
}
