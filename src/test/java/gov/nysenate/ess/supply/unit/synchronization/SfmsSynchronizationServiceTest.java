package gov.nysenate.ess.supply.unit.synchronization;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.supply.InMemoryRequisitionDao;
import gov.nysenate.ess.supply.InMemorySyncAttemptDao;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.Category;
import gov.nysenate.ess.supply.item.model.ItemAllowance;
import gov.nysenate.ess.supply.item.model.ItemStatus;
import gov.nysenate.ess.supply.item.model.ItemUnit;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.notification.SupplyEmailService;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.model.*;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.service.SupplyRequisitionService;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;
import gov.nysenate.ess.supply.requisition.view.SfmsRequisitionView;
import gov.nysenate.ess.supply.synchronization.SyncStatuses.ModifySyncStatus;
import gov.nysenate.ess.supply.synchronization.dao.RequisitionSyncAttemptDao;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncAttempt;
import gov.nysenate.ess.supply.synchronization.model.RequisitionSyncResult;
import gov.nysenate.ess.supply.synchronization.service.SfmsSynchronizationService;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import gov.nysenate.ess.supply.util.date.DummyDateTime;
import org.apache.shiro.dao.DataAccessException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class SfmsSynchronizationServiceTest {

    @Mock
    private SfmsSynchronizationProcedure synchronizationProcedure;
    @Mock
    private SlackChatService slackChatService;
    @Mock
    private SupplyEmailService emailService;

    private DummyDateTime dummyDateTime;
    private RequisitionService requisitionService;
    private SfmsSynchronizationService service;


    private final InMemorySyncAttemptDao inMemorySyncAttemptDao = new InMemorySyncAttemptDao();


    @Before
    public void setup() {
        requisitionService = new SupplyRequisitionService(new InMemoryRequisitionDao(), emailService);
        dummyDateTime = new DummyDateTime();
        service = new SfmsSynchronizationService(
                true,
                requisitionService,
                synchronizationProcedure,
                dummyDateTime,
                slackChatService,
                inMemorySyncAttemptDao
        );

    }

    @Test
    public void givenSyncDisabled_thenSkipSync() {
        service = new SfmsSynchronizationService(
                false,
                requisitionService,
                synchronizationProcedure,
                dummyDateTime,
                slackChatService,
                inMemorySyncAttemptDao
        );
        service.synchronizeRequisitions();
        verifyNoInteractions(synchronizationProcedure, slackChatService);
    }


    @Test
    public void testSuccessfulSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1001, setOf(requiresSyncItem(1, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        RequisitionSyncResult result = service.syncRequisition(requisition);

        // Check for valid side effects.
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.COMPLETE, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());


        assertEquals(SyncStatus.COMPLETE, result.getSyncAttempt().getOutcomeSyncStatus());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertTrue(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncableLineItems().size());
    }

    @Test
    public void testNonSyncableItemNotSynced() {
        Requisition requisition = buildRequisition(1001, setOf(requiresSyncItem(1, 1), doesNotRequireSyncItem(2, 1)));
        ArgumentCaptor<String> requisitionXmlCaptor = ArgumentCaptor.forClass(String.class);

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);
        verify(synchronizationProcedure).synchronizeRequisition(requisitionXmlCaptor.capture());
        String capturedRequisitionXml = requisitionXmlCaptor.getValue();

        // Check for valid side effects.
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.COMPLETE, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());

        assertEquals(1001, result.getSyncAttempt().getRequisitionId());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result.getSyncAttempt().getAttemptSyncDate());
        assertTrue(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo()); // you decide if error info should be null or an empty string when there was no error then enforce consistently
        assertEquals(SyncStatus.COMPLETE, result.getSyncAttempt().getOutcomeSyncStatus());
        assertEquals(new ArrayList<>(List.of(1)), result.getSyncAttempt().getSyncableLineItems());
        System.out.println(capturedRequisitionXml);
        assertTrue(capturedRequisitionXml.contains("<itemId>1</itemId>"));
        assertFalse(capturedRequisitionXml.contains("<itemId>2</itemId>"));
    }

    @Test
    public void testZeroQuantityItemsNotSynced() {
        Requisition requisition = buildRequisition(1001, setOf(requiresSyncItem(1, 1), requiresSyncItem(2, 0)));
        ArgumentCaptor<String> requisitionXmlCaptor = ArgumentCaptor.forClass(String.class);

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);
        verify(synchronizationProcedure).synchronizeRequisition(requisitionXmlCaptor.capture());
        String capturedRequisitionXml = requisitionXmlCaptor.getValue();

        // Check for valid side effects.
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.COMPLETE, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());

        assertEquals(1001, result.getSyncAttempt().getRequisitionId());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result.getSyncAttempt().getAttemptSyncDate());
        assertTrue(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo()); // you decide if error info should be null or an empty string when there was no error then enforce consistently
        assertEquals(SyncStatus.COMPLETE, result.getSyncAttempt().getOutcomeSyncStatus());
        assertEquals(new ArrayList<>(List.of(1)), result.getSyncAttempt().getSyncableLineItems());
        System.out.println(result.getSyncAttempt().getSyncableLineItems());

        System.out.print(requisitionXmlCaptor);
        assertTrue(capturedRequisitionXml.contains("<itemId>1</itemId>"));
        assertFalse(capturedRequisitionXml.contains("<itemId>2</itemId>"));
    }

    @Test
    public void testRejectedSync() {
        // Initialize test state.
        Requisition requisition = buildRejectedRequisition(1002, setOf(requiresSyncItem(2, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        RequisitionSyncResult result = service.syncRequisition(requisition);

        verify(synchronizationProcedure, never()).synchronizeRequisition(any());
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertEquals(SkippedReason.REJECTED, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());


        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncableLineItems().size());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
    }


    @Test
    public void test_No_Syncable_Items_And_Rejected_Sync() {
        Requisition requisition = buildRejectedRequisition(1003, setOf(doesNotRequireSyncItem(2, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        RequisitionSyncResult result = service.syncRequisition(requisition);

        verify(synchronizationProcedure, never()).synchronizeRequisition(any());
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNotNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(SkippedReason.REJECTED, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());

        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(0, result.getSyncAttempt().getSyncableLineItems().size());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
    }

    @Test
    public void test_No_Syncable_Items_Sync() {
        Requisition requisition = buildRequisition(1004, setOf(doesNotRequireSyncItem(1, 3), doesNotRequireSyncItem(2, 2)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        RequisitionSyncResult result = service.syncRequisition(requisition);

        verify(synchronizationProcedure, never()).synchronizeRequisition(any());
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNotNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(SkippedReason.NO_SYNCABLE_ITEMS, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());

        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(0, result.getSyncAttempt().getSyncableLineItems().size());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
    }

    @Test
    public void testErrorSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1005, setOf(requiresSyncItem(1, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        doThrow(new DataAccessResourceFailureException("Database failed to save")).when(synchronizationProcedure).synchronizeRequisition(any());

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);

        // Check for valid side effects.
        assertEquals(SyncStatus.ERROR, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());
        assertNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());

        assertEquals(SyncStatus.ERROR, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNotNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncableLineItems().size());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result.getSyncAttempt().getAttemptSyncDate());
    }

    @Test
    public void testReqWithZeroQuantitySync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1005, setOf(requiresSyncItem(1, 0)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);

        verify(synchronizationProcedure, never()).synchronizeRequisition(any());
        assertEquals(expectedSyncDateTime, result.getUpdatedRequisition().getLastSfmsSyncDateTime().get());
        assertEquals(SyncStatus.SKIPPED, result.getUpdatedRequisition().getSfmsSyncStatus());
        assertNotNull(result.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(SkippedReason.NO_SYNCABLE_ITEMS, result.getUpdatedRequisition().getSfmsSkippedReason());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getUpdatedRequisition().getSfmsSyncAttempts());


        assertEquals(SyncStatus.SKIPPED, result.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result.getSyncAttempt().getWasSuccessful());
        assertNull(result.getSyncAttempt().getErrorInfo());
        assertEquals(1, result.getSyncAttempt().getSyncAttempts());
        assertEquals(0, result.getSyncAttempt().getSyncableLineItems().size());
        assertNotNull(result.getSyncAttempt().getAttemptSyncDate());
    }

    @Test // Can't test this due to pessimistic lock issue with modified date time
    public void testMultipleErrorSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1006, setOf(requiresSyncItem(1, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);

        doThrow(new DataAccessResourceFailureException("Database failed to save")).when(synchronizationProcedure).synchronizeRequisition(any());

        // Execute method to test
        RequisitionSyncResult result = service.syncRequisition(requisition);
        RequisitionSyncResult result1 = service.syncRequisition(result.getUpdatedRequisition());
        RequisitionSyncResult result2 = service.syncRequisition(result1.getUpdatedRequisition());
        RequisitionSyncResult result3 = service.syncRequisition(result2.getUpdatedRequisition());

        assertEquals(SyncStatus.ERROR, result3.getUpdatedRequisition().getSfmsSyncStatus());
        assertEquals(4, result3.getUpdatedRequisition().getSfmsSyncAttempts());
        assertNull(result3.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(expectedSyncDateTime, result3.getUpdatedRequisition().getLastSfmsSyncDateTime().get());

        assertEquals(SyncStatus.ERROR, result3.getSyncAttempt().getOutcomeSyncStatus());
        assertFalse(result3.getSyncAttempt().getWasSuccessful());
        assertNotNull(result3.getSyncAttempt().getErrorInfo());
        assertEquals(1, result3.getSyncAttempt().getSyncableLineItems().size());
        assertEquals(4, result3.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result3.getSyncAttempt().getAttemptSyncDate());
    }


    @Test // Can't test this due to pessimistic lock issue with modified date time
    public void testMultipleErrorSyncThenSucceed() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1007, setOf(requiresSyncItem(1, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime); // Fetch state after execution.

        // Check for valid side effects.


        // Execute method to test
        doThrow(new DataAccessResourceFailureException("Database failed to save")).doThrow(new DataAccessResourceFailureException("Database failed to save")).doThrow(new DataAccessResourceFailureException("Database failed to save")).doThrow(new DataAccessResourceFailureException("Database failed to save")).doNothing().when(synchronizationProcedure).synchronizeRequisition(any());


        RequisitionSyncResult result = service.syncRequisition(requisition);
        RequisitionSyncResult result1 = service.syncRequisition(result.getUpdatedRequisition());
        RequisitionSyncResult result2 = service.syncRequisition(result1.getUpdatedRequisition());
        RequisitionSyncResult result3 = service.syncRequisition(result2.getUpdatedRequisition());
        RequisitionSyncResult result4 = service.syncRequisition(result3.getUpdatedRequisition());

        assertEquals(SyncStatus.COMPLETE, result4.getUpdatedRequisition().getSfmsSyncStatus());
        assertEquals(5, result4.getUpdatedRequisition().getSfmsSyncAttempts());
        assertNull(result4.getUpdatedRequisition().getSfmsSkippedReason());
        assertEquals(expectedSyncDateTime, result4.getUpdatedRequisition().getLastSfmsSyncDateTime().get());

        assertEquals(SyncStatus.COMPLETE, result4.getSyncAttempt().getOutcomeSyncStatus());
        assertTrue(result4.getSyncAttempt().getWasSuccessful());
        assertNull(result4.getSyncAttempt().getErrorInfo());
        assertEquals(1, result4.getSyncAttempt().getSyncableLineItems().size());
        assertEquals(5, result4.getSyncAttempt().getSyncAttempts());
        assertEquals(expectedSyncDateTime, result4.getSyncAttempt().getAttemptSyncDate());

        // Fetch state after execution.

        // Check for valid side effects.
    }

    @Test
    public void testLineItemsEqualInOriginalAndUpdatedSync() {
        // Initialize test state.
        Requisition requisition = buildRequisition(1001, setOf(requiresSyncItem(1, 1)));

        LocalDateTime expectedSyncDateTime = LocalDateTime.now();
        dummyDateTime.setDateTime(expectedSyncDateTime);


        RequisitionSyncResult result = service.syncRequisition(requisition);


        // Check for valid side effects.

        List<Integer> originalLineItems = new ArrayList<>();
        for (LineItem item : requisition.getLineItems()) {
            originalLineItems.add(item.getItem().getId());
        }

        List<Integer> updatedLineItems = new ArrayList<>();
        for (LineItem item : result.getUpdatedRequisition().getLineItems()) {
            updatedLineItems.add(item.getItem().getId());
        }

        for (int i = 0; i < originalLineItems.size(); i++) {
            assertEquals(originalLineItems.get(i), updatedLineItems.get(i));
        }
    }

    private Requisition buildRequisition(int requisitionId, Set<LineItem> lineItems) {
        Employee customer = new Employee();
        customer.setEmployeeId(10);

        Employee issuer = new Employee();
        issuer.setEmployeeId(20);
        issuer.setUid("issuer" + requisitionId);

        Location destination = new Location(new LocationId("A42FB", 'W'));
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

        return new Requisition.Builder()
                .withRequisitionId(requisitionId)
                .withRevisionId(1)
                .withCustomer(customer)
                .withDestination(destination)
                .withDeliveryMethod(DeliveryMethod.DELIVERY)
                .withLineItems(lineItems)
                .withState(new ApprovedState())
                .withIssuer(issuer)
                .withModifiedBy(customer)
                .withModifiedDateTime(now)
                .withOrderedDateTime(now)
                .withProcessedDateTime(LocalDateTime.now())
                .withCompletedDateTime(LocalDateTime.now())
                .withApprovedDateTime(LocalDateTime.now())
                .build();
    }

    private Requisition buildRejectedRequisition(int requisitionId, Set<LineItem> lineItems) {
        Employee customer = new Employee();
        customer.setEmployeeId(11);

        Employee issuer = new Employee();
        issuer.setEmployeeId(21);
        issuer.setUid("issuer" + requisitionId);

        Location destination = new Location(new LocationId("A42FB", 'W'));
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);

        return new Requisition.Builder()
                .withRequisitionId(requisitionId)
                .withRevisionId(1)
                .withCustomer(customer)
                .withDestination(destination)
                .withDeliveryMethod(DeliveryMethod.DELIVERY)
                .withLineItems(lineItems)
                .withState(new RejectedState())
                .withIssuer(issuer)
                .withModifiedBy(customer)
                .withModifiedDateTime(now)
                .withOrderedDateTime(now)
                .withProcessedDateTime(LocalDateTime.now())
                .withCompletedDateTime(LocalDateTime.now())
                .withRejectedDateTime(LocalDateTime.now())
                .build();
    }

    private static LineItem requiresSyncItem(int itemId, int quantity) {
        return lineItem(itemId, quantity, true);
    }

    private static LineItem doesNotRequireSyncItem(int itemId, int quantity) {
        return lineItem(itemId, quantity, false);
    }

    private static LineItem lineItem(int itemId, int quantity, boolean requiresSync) {
        ItemStatus status = requiresSync
                ? new ItemStatus(true, false, true, false)
                : new ItemStatus(true, true, true, false);
        SupplyItem item = new SupplyItem.Builder()
                .withId(itemId)
                .withCommodityCode("X")
                .withDescription("Test Item")
                .withStatus(status)
                .withCategory(new Category("Test"))
                .withAllowance(new ItemAllowance(10, 100))
                .withUnit(new ItemUnit("EA", 1))
                .withReconciliationPage(1)
                .build();
        return new LineItem(item, quantity);
    }

    private static Set<LineItem> setOf(LineItem... items) {
        Set<LineItem> set = new HashSet<>();
        Collections.addAll(set, items);
        return set;
    }
}
