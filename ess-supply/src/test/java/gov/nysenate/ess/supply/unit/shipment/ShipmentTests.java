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

import static oracle.net.aso.C01.m;
import static oracle.net.aso.C01.o;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ShipmentTests extends SupplyUnitTests {

    protected static Location stubLocation;
    protected static Employee stubEmployee;
    protected static Order stubOrder;

    protected Shipment shipment;
    protected ShipmentVersion pendingVersion;
    protected LocalDateTime pendingDateTime;

    @BeforeClass
    public static void setupClass() {
        stubLocation = new Location(new LocationId("A42FB", 'W'));
        stubEmployee = new Employee();
        stubEmployee.setEmployeeId(1);
        stubOrder = createStubOrder();
    }

    private static Order createStubOrder() {
        SupplyItem stubItem = new SupplyItem(1, "", "", "", new Category(""), 1, 1);
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


    private ShipmentVersion createUniqueVersionWithStatus(ShipmentStatus status) {
        Employee issuer = new Employee();
        issuer.setEmployeeId(22);
        Employee modifiedBy = new Employee();
        modifiedBy.setEmployeeId(52);
        return new ShipmentVersion.Builder().withIssuingEmployee(issuer).withModifiedBy(modifiedBy)
                                            .withStatus(status).build();
    }

    public class GivenProcessingVersion {
        protected ShipmentVersion processingVersion;
        protected LocalDateTime processingDateTime;

        @Before
        public void addProcessingVersion() {
            processingVersion = new ShipmentVersion.Builder().withIssuingEmployee(stubEmployee).withModifiedBy(stubEmployee)
                                                             .withStatus(ShipmentStatus.PROCESSING).build();
            processingDateTime = pendingDateTime.plusMinutes(5);
            shipment = shipment.addVersion(processingVersion, processingDateTime);
        }

        @Test
        public void newlyAddedVersionIsCurrentVersion() {
            //TODO:
//            ShipmentVersion processing = createUniqueVersionWithStatus(ShipmentStatus.PROCESSING);
//            shipment = shipment.addVersion(processing, LocalDateTime.now());
//            assertThat(shipment.getStatus(), is(processing.getStatus()));
//            assertThat(shipment.getIssuingEmployee(), is(processing.getIssuingEmployee()));
//            assertThat(shipment.getModifiedBy(), is(processing.getModifiedBy()));
        }

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
            public void canGetDateTimes() {
                assertThat(shipment.getCreatedDateTime(), is(pendingDateTime));
                assertThat(shipment.getProcessedDateTime().get(), is(processingDateTime));
                assertThat(shipment.getCompletedDateTime().get(), is(completedDateTime));
            }

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
