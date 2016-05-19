package gov.nysenate.ess.supply.unit.requisition;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.SupplyUnitTests;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequisitionTests extends SupplyUnitTests {

    private static final LocalDateTime orderedDateTime = LocalDateTime.now();

    private static Set<LineItem> createStubLineItem() {
        SupplyItem stubItem = new SupplyItem(1, "", "", "", new Category(""), 1, 1, 1);
        Set<LineItem> stubLineItems = new HashSet<>();
        stubLineItems.add(new LineItem(stubItem, 1));
        return stubLineItems;
    }

    @Test(expected = IllegalArgumentException.class)
    public void firstVersionMustHavePendingStatus() {
        RequisitionStatus status = RequisitionStatus.COMPLETED;
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(new Employee())
                .withDestination(new Location(new LocationId("A42FB", 'W')))
                .withStatus(status)
                .withLineItems(createStubLineItem())
                .withModifiedBy(new Employee())
                .build();
        Requisition requisition = new Requisition(orderedDateTime, version);
    }

    @Test(expected = NullPointerException.class)
    public void orderDateTimeCannotBeNull() {
        RequisitionVersion version = new RequisitionVersion.Builder()
                .withId(1)
                .withCustomer(new Employee())
                .withDestination(new Location(new LocationId("A42FB", 'W')))
                .withStatus(RequisitionStatus.PENDING)
                .withLineItems(createStubLineItem())
                .withModifiedBy(new Employee())
                .build();
        Requisition requisition = new Requisition(null, version);
    }

    public class GivenPendingVersion {
        private Requisition requisition;
        private RequisitionVersion pendingRequisition;
        private Location destination;
        private Employee customer;
        private Employee issuer;
        private Set<LineItem> lineItems;
        private String note;
        private RequisitionStatus status;

        @Before
        public void createRequisitionWithPendingVersion() {
            destination = new Location(new LocationId("A42FB", 'W'));
            customer = new Employee();
            customer.setEmployeeId(1);
            issuer = new Employee();
            issuer.setEmployeeId(2);
            lineItems = createStubLineItem();
            note = "A note";
            status = RequisitionStatus.PENDING;
            pendingRequisition = new RequisitionVersion.Builder()
                    .withId(1)
                    .withCustomer(customer)
                    .withDestination(destination)
                    .withStatus(status)
                    .withLineItems(lineItems)
                    .withIssuer(issuer)
                    .withModifiedBy(customer)
                    .withNote(note)
                    .build();
            requisition = new Requisition(orderedDateTime, pendingRequisition);
        }

        @Test
        public void currentVersionIsPendingVersion() {
            assertThat(requisition.getCurrentVersion(), is(pendingRequisition));
        }

        @Test
        public void gettersReturnCurrentVersionFields() {
            RequisitionVersion currentVersion = requisition.getCurrentVersion();
            assertThat(requisition.getCustomer(), is(currentVersion.getCustomer()));
            assertThat(requisition.getDestination(), is(currentVersion.getDestination()));
            assertThat(requisition.getLineItems(), is(currentVersion.getLineItems()));
            assertThat(requisition.getStatus(), is(currentVersion.getStatus()));
            assertThat(requisition.getIssuer(), is(currentVersion.getIssuer()));
            assertThat(requisition.getModifiedBy(), is(currentVersion.getModifiedBy()));
            assertThat(requisition.getNote(), is(currentVersion.getNote()));
        }

        @Test
        public void canGetOrderedDateTime() {
            assertThat(requisition.getOrderedDateTime(), is(orderedDateTime));
        }

        @Test
        public void modifiedDateTime_equalsOrderedDateTime() {
            assertThat(requisition.getModifiedDateTime(), is(orderedDateTime));
        }

        public class AddingProcessingVersion {
            private RequisitionVersion processingVersion;
            private LocalDateTime processedDateTime;
            private Employee processedBy;
            private RequisitionStatus processingStatus;

            @Before
            public void addProcessingVersion() {
                processedDateTime = orderedDateTime.plusMinutes(5);
                processedBy = new Employee();
                processedBy.setEmployeeId(3);
                processingStatus = RequisitionStatus.PROCESSING;
                processingVersion = new RequisitionVersion.Builder()
                        .withId(2)
                        .withCustomer(customer)
                        .withDestination(destination)
                        .withStatus(processingStatus)
                        .withLineItems(lineItems)
                        .withIssuer(issuer)
                        .withModifiedBy(processedBy)
                        .build();
                requisition.addVersion(processedDateTime, processingVersion);
            }

            @Test
            public void currentVersionIsProcessing() {
                assertThat(requisition.getCurrentVersion(), is(processingVersion));
            }

            @Test
            public void twoVersionInHistory() {
                assertThat(requisition.getHistory().size(), is(2));
            }

            @Test
            public void dateTimesAreUpdated() {
                assertThat(requisition.getProcessedDateTime().get(), is(processedDateTime));
                assertThat(requisition.getModifiedDateTime(), is(processedDateTime));
                assertThat(requisition.getOrderedDateTime(), is(orderedDateTime));
            }

            public class AddRejectedVersion {
                private RequisitionVersion rejectedVersion;
                private LocalDateTime rejectedDateTime;

                @Before
                public void addRejectedVersion() {
                    rejectedDateTime = processedDateTime.plusMinutes(2);
                    rejectedVersion = new RequisitionVersion.Builder()
                            .withId(3)
                            .withCustomer(customer)
                            .withDestination(destination)
                            .withStatus(RequisitionStatus.REJECTED)
                            .withLineItems(lineItems)
                            .withIssuer(issuer)
                            .withModifiedBy(processedBy)
                            .build();
                    requisition.addVersion(rejectedDateTime, rejectedVersion);
                }

                @Test
                public void currentVersionIsRejected() {
                    assertThat(requisition.getCurrentVersion(), is(rejectedVersion));
                }

                @Test
                public void dateTimesAreUpdated() {
                    assertThat(requisition.getRejectedDateTime().get(), is(rejectedDateTime));
                    assertThat(requisition.getModifiedDateTime(), is(rejectedDateTime));
                    assertThat(requisition.getProcessedDateTime().get(), is(processedDateTime));
                    assertThat(requisition.getOrderedDateTime(), is(orderedDateTime));
                }

            }

            public class AddingCompletedVersion {
                private RequisitionVersion completedVersion;
                private LocalDateTime completedDateTime;
                private RequisitionStatus completedStatus;

                @Before
                public void addCompletedVersion() {
                    completedDateTime = processedDateTime.plusMinutes(5);
                    completedStatus = RequisitionStatus.COMPLETED;
                    completedVersion = new RequisitionVersion.Builder()
                            .withId(3)
                            .withCustomer(customer)
                            .withDestination(destination)
                            .withStatus(completedStatus)
                            .withLineItems(lineItems)
                            .withIssuer(issuer)
                            .withModifiedBy(processedBy)
                            .build();
                    requisition.addVersion(completedDateTime, completedVersion);
                }

                @Test
                public void currentVersionIsCompleted() {
                    assertThat(requisition.getCurrentVersion(), is(completedVersion));
                }

                @Test
                public void dateTimesAreUpdated() {
                    assertThat(requisition.getCompletedDateTime().get(), is(completedDateTime));
                    assertThat(requisition.getModifiedDateTime(), is(completedDateTime));
                    assertThat(requisition.getProcessedDateTime().get(), is(processedDateTime));
                    assertThat(requisition.getOrderedDateTime(), is(orderedDateTime));
                }

                public class AddingApprovedVersion {
                    private RequisitionVersion approvedVersion;
                    private LocalDateTime approvedDateTime;

                    @Before
                    public void addApprovedVersion() {
                        approvedDateTime = completedDateTime.plusMinutes(5);
                        approvedVersion = new RequisitionVersion.Builder()
                            .withId(4)
                            .withCustomer(customer)
                            .withDestination(destination)
                            .withStatus(RequisitionStatus.APPROVED)
                            .withLineItems(lineItems)
                            .withIssuer(issuer)
                            .withModifiedBy(processedBy)
                            .build();
                        requisition.addVersion(approvedDateTime, approvedVersion);
                    }

                    @Test
                    public void currentVersionIsApproved() {
                        assertThat(requisition.getCurrentVersion(), is(approvedVersion));
                    }

                    @Test
                    public void dateTimesAreUpdated() {
                        assertThat(requisition.getApprovedDateTime().get(), is(approvedDateTime));
                        assertThat(requisition.getModifiedDateTime(), is(approvedDateTime));
                        assertThat(requisition.getCompletedDateTime().get(), is(completedDateTime));
                        assertThat(requisition.getProcessedDateTime().get(), is(processedDateTime));
                        assertThat(requisition.getOrderedDateTime(), is(orderedDateTime));
                    }
                }
            }
        }
    }
}
