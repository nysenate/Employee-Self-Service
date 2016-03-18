package gov.nysenate.ess.supply.integration.shipment;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import gov.nysenate.ess.supply.shipment.dao.ShipmentDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

//@Transactional
//@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class ShipmentDaoTests extends SupplyTests {

    @Autowired private ShipmentDao shipmentDao;
    @Autowired private EmployeeInfoService employeeService;
    @Autowired private LocationService locationService;
    @Autowired private SupplyItemDao itemDao;

    private Order order;
    private LocalDateTime insertedDateTime;
    private ShipmentVersion version;
    private ShipmentHistory history;

    private static final Range<LocalDateTime> LAST_YEAR = Range.closed(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusMinutes(5));

    @Before
    public void setup() {
        /** Create Order */
        Employee customer = employeeService.getEmployee(6221);
        Location destination = locationService.getLocation("A42FB-W");
        Set<LineItem> lineItems = new HashSet<>();
        lineItems.add(new LineItem(itemDao.getItemById(1), 3));
        lineItems.add(new LineItem(itemDao.getItemById(2), 3));

        OrderVersion firstVersion = new OrderVersion.Builder()
                .withCustomer(customer)
                .withDestination(destination)
                .withLineItems(lineItems)
                .withStatus(OrderStatus.APPROVED)
                .withModifiedBy(customer)
                .build();

        order = Order.of(1, OrderHistory.of(LocalDateTime.now(), firstVersion));

        /** Shipment setup */
        Employee modifiedBy = employeeService.getEmployee(6221);
        insertedDateTime = LocalDateTime.now().minusHours(5);
        version = new ShipmentVersion.Builder().withStatus(ShipmentStatus.PENDING)
                .withModifiedBy(modifiedBy).build();
        history = ShipmentHistory.of(insertedDateTime, version);
    }

    @Test
    public void canInsertShipment() {
        int shipmentId = shipmentDao.insert(order, version, insertedDateTime);
        assertTrue(shipmentId > 0);
    }

    @Test
    public void canGetShipmentById() {
        int shipmentId = shipmentDao.insert(order, version, insertedDateTime);
        Shipment shipment = shipmentDao.getById(shipmentId);
        assertEquals(history, shipment.getHistory());
    }

    @Test
    public void canSaveShipment() {
        int shipmentId = shipmentDao.insert(order, version, insertedDateTime);
        Shipment shipment = shipmentDao.getById(shipmentId);
        Employee issuingEmp = employeeService.getEmployee(10012);
        LocalDateTime modifiedDateTime = insertedDateTime.plusHours(1);
        shipment = shipment.process(issuingEmp, issuingEmp, modifiedDateTime);
        shipmentDao.save(shipment);
        assertEquals(shipment, shipmentDao.getById(shipment.getId()));
        assertEquals(ShipmentStatus.PROCESSING, shipmentDao.getById(shipment.getId()).getStatus());
    }

    @Test
    public void canGetShipmentsByStatus() {
        int shipmentId = shipmentDao.insert(order, version, insertedDateTime);
        Shipment shipment = shipmentDao.getById(shipmentId);
        Employee issuingEmp = employeeService.getEmployee(10012);
        LocalDateTime modifiedDateTime = insertedDateTime.plusHours(1);
        shipment = shipment.process(issuingEmp, issuingEmp, modifiedDateTime);
        shipmentDao.save(shipment);

        PaginatedList<Shipment> shipmentList = shipmentDao.getShipments("all", EnumSet.of(ShipmentStatus.PROCESSING), LAST_YEAR, LimitOffset.ALL);
        assertEquals(shipment, shipmentList.getResults().get(shipmentList.getResults().size() - 1));
    }
}
