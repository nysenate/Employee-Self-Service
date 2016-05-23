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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static oracle.net.aso.C01.p;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RequisitionTests extends SupplyUnitTests {

    private static final LocalDateTime orderedDateTime = LocalDateTime.now();

    @Test(expected = IllegalArgumentException.class)
    public void firstVersionMustHavePendingStatus() {
        RequisitionVersion version = RequisitionFixture.getCompletedVersion();
        Requisition requisition = new Requisition(orderedDateTime, version);
    }

    @Test(expected = NullPointerException.class)
    public void orderDateTimeCannotBeNull() {
        RequisitionVersion version = RequisitionFixture.getPendingVersion();
        Requisition requisition = new Requisition(null, version);
    }

    public class GivenPendingVersion {
        private Requisition requisition;
        private RequisitionVersion pendingRequisition;

        @Before
        public void addPendingVersion() {
            pendingRequisition = RequisitionFixture.getPendingVersion();
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

            @Before
            public void addProcessingVersion() {
                processedDateTime = orderedDateTime.plusMinutes(5);
                processingVersion = RequisitionFixture.getProcessingVersion();
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
                    rejectedVersion = RequisitionFixture.getRejectedVersion();
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

                @Test
                public void canGetLatestNonRejectedVersion() {
                    RequisitionVersion actual = requisition.getLatestVersionWithStatusIn(EnumSet.complementOf(EnumSet.of(RequisitionStatus.REJECTED)));
                    assertThat(actual, is(processingVersion));
                }

            }

            public class AddingCompletedVersion {
                private RequisitionVersion completedVersion;
                private LocalDateTime completedDateTime;

                @Before
                public void addCompletedVersion() {
                    completedDateTime = processedDateTime.plusMinutes(5);
                    completedVersion = RequisitionFixture.getCompletedVersion();
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
                        approvedVersion = RequisitionFixture.getApprovedVersion();
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
