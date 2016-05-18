package gov.nysenate.ess.supply.unit.shipment;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.SupplyUnitTests;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import gov.nysenate.ess.supply.shipment.Shipment;
import gov.nysenate.ess.supply.shipment.ShipmentHistory;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ShipmentTests extends SupplyUnitTests {

    private static Location stubLocation;
    private static Employee stubEmployee;
    private static Employee issuingEmployee;
    private static Employee modifiedByEmployee;
    private static Order stubOrder;

    private Shipment shipment;
    private ShipmentVersion pendingVersion;
    private LocalDateTime pendingDateTime;

    @BeforeClass
    public static void setupClass() {
        stubLocation = new Location(new LocationId("A42FB", 'W'));
        stubEmployee = createEmployeeWithId(1);
        issuingEmployee = createEmployeeWithId(2);
        modifiedByEmployee = createEmployeeWithId(3);
        stubOrder = createStubOrder();
    }

    private static Employee createEmployeeWithId(int id) {
        Employee emp = new Employee();
        emp.setEmployeeId(id);
        return emp;
    }

    private static Order createStubOrder() {
        SupplyItem stubItem = new SupplyItem(1, "", "", "", new Category(""), 1, 1, 1);
        Set<LineItem> stubLineItems = new HashSet<>();
        stubLineItems.add(new LineItem(stubItem, 1));
        OrderVersion orderVersion = new OrderVersion.Builder().withId(1).withCustomer(stubEmployee).withDestination(stubLocation)
                                                              .withModifiedBy(stubEmployee).withStatus(OrderStatus.APPROVED).withLineItems(stubLineItems).build();
        return Order.of(1, OrderHistory.of(LocalDateTime.now(), orderVersion));
    }

    @Before
    public void createShipmentWithPendingVersion() {
        pendingVersion = new ShipmentVersion.Builder().withId(1).withModifiedBy(stubEmployee)
                                                      .withStatus(ShipmentStatus.PENDING).build();
        pendingDateTime = LocalDateTime.now();
        shipment = Shipment.of(1, stubOrder, ShipmentHistory.of(pendingDateTime, pendingVersion));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantAddCompletedVersion() {
        ShipmentVersion completed = new ShipmentVersion.Builder().withIssuingEmployee(stubEmployee).withModifiedBy(stubEmployee)
                                                                 .withStatus(ShipmentStatus.COMPLETED).build();
        LocalDateTime completedDateTime = pendingDateTime.plusMinutes(5);
        shipment.addVersion(completed, completedDateTime);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantAddApprovedVersion() {
        ShipmentVersion approved = new ShipmentVersion.Builder().withIssuingEmployee(stubEmployee).withModifiedBy(stubEmployee)
                                                                .withStatus(ShipmentStatus.APPROVED).build();
        LocalDateTime approvedDateTime = pendingDateTime.plusMinutes(5);
        shipment.addVersion(approved, approvedDateTime);
    }

    @Test
    public void gettersDelegateToCurrentVersion() {
        assertThat(shipment.getStatus(), is(pendingVersion.getStatus()));
        assertThat(shipment.getIssuingEmployee(), is(pendingVersion.getIssuingEmployee()));
        assertThat(shipment.getModifiedBy(), is(pendingVersion.getModifiedBy()));
    }

    public class GivenProcessingVersion {
        private ShipmentVersion processingVersion;
        private LocalDateTime processingDateTime;

        @Before
        public void addProcessingVersion() {
            processingVersion = new ShipmentVersion.Builder().withIssuingEmployee(issuingEmployee).withModifiedBy(modifiedByEmployee)
                                                             .withStatus(ShipmentStatus.PROCESSING).build();
            processingDateTime = pendingDateTime.plusMinutes(5);
            shipment = shipment.addVersion(processingVersion, processingDateTime);
        }

        @Test
        public void canGetProcessingDateTime() {
            assertThat(shipment.getProcessedDateTime().get(), is(processingDateTime));
        }

        @Test
        public void createdDateTimeIsUnchanged() {
            assertThat(shipment.getCreatedDateTime(), is(pendingDateTime));
        }

        @Test
        public void modifiedDateTimeIsUpdated() {
            assertThat(shipment.getModifiedDateTime(), is(processingDateTime));
        }

        @Test
        public void currentVersionIsProcessingVersion() {
            assertThat(shipment.current(), is(processingVersion));
        }

        @Test
        public void shipmentHasTwoVersions() {
            assertThat(shipment.getHistory().getHistory().size(), is(2));
        }

        @Test
        public void gettersReturnProcessingVersionInfo() {
            assertThat(shipment.getStatus(), is(processingVersion.getStatus()));
            assertThat(shipment.getIssuingEmployee(), is(processingVersion.getIssuingEmployee()));
            assertThat(shipment.getModifiedBy(), is(processingVersion.getModifiedBy()));
        }

        @Test(expected = IllegalArgumentException.class)
        public void cantAddApprovedVersion() {
            ShipmentVersion approved = new ShipmentVersion.Builder().withIssuingEmployee(stubEmployee).withModifiedBy(stubEmployee)
                                                                    .withStatus(ShipmentStatus.APPROVED).build();
            LocalDateTime approvedDateTime = processingDateTime.plusMinutes(5);
            shipment.addVersion(approved, approvedDateTime);
        }

//        @Test(expected = IllegalArgumentException.class)
//        public void cantAddPendingVersion() {
//            ShipmentVersion pending = new ShipmentVersion.Builder().withModifiedBy(stubEmployee).withStatus(ShipmentStatus.PENDING).build();
//            LocalDateTime pendingDateTime = processingDateTime.plusMinutes(5);
//            shipment.addVersion(pending, pendingDateTime);
//        }

        public class GivenCompletedVersion {
            protected ShipmentVersion completedVersion;
            protected LocalDateTime completedDateTime;

            @Before
            public void addCompletedVersion() {
                completedVersion = new ShipmentVersion.Builder().withIssuingEmployee(stubEmployee).withModifiedBy(stubEmployee)
                                                                .withStatus(ShipmentStatus.COMPLETED).build();
                completedDateTime = processingDateTime.plusMinutes(5);
                shipment = shipment.addVersion(completedVersion, completedDateTime);
            }

            @Test
            public void canGetCompletedDateTime() {
                assertThat(shipment.getCompletedDateTime().get(), is(completedDateTime));
            }

//            public class GivenShipmentIsCanceled

//        @Test
//        public void givenCanceledShipmentIsAccepted_returnsToPreviousStatus() {
//            shipment = shipment.addVersion(canceledVersion, canceledDateTime);
//            shipment = shipment.addVersion(acceptedVersion, acceptedDateTime);
//        }
//
//        @Test
//        public void givenMultipleCompletedVersions_completedDateTimeReturnsTimeOfFirstVersion() {
//            shipment = shipment.addVersion(canceledVersion, canceledDateTime);
//            shipment = shipment.addVersion(acceptedVersion, acceptedDateTime);
//        }
        }
    }
}
