package gov.nysenate.ess.supply.integration.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.item.service.SupplyItemService;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import gov.nysenate.ess.supply.shipment.dao.ShipmentDao;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class ShipmentDaoTests extends SupplyTests {

    @Autowired private ShipmentDao shipmentDao;
    @Autowired private EmployeeInfoService employeeService;
    @Autowired private LocationService locationService;
    @Autowired private SupplyItemDao itemDao;

    private Order order;

    @Before
    public void setup() {
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

        order = Order.of(20, OrderHistory.of(LocalDateTime.now(), firstVersion));
    }

    @Test
    public void canInsertShipment() {
        Employee modifiedBy = employeeService.getEmployee(6221);
        LocalDateTime modifiedDateTime = LocalDateTime.now();
        ShipmentVersion version = new ShipmentVersion.Builder().withStatus(ShipmentStatus.PENDING)
                .withModifiedBy(modifiedBy).build();
        int shipmentId = shipmentDao.insert(order, version, modifiedDateTime);
        assertTrue(shipmentId > 0);
    }
}
