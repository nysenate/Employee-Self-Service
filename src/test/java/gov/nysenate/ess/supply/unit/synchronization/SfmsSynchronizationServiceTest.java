package gov.nysenate.ess.supply.unit.synchronization;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.Category;
import gov.nysenate.ess.supply.item.model.ItemAllowance;
import gov.nysenate.ess.supply.item.model.ItemStatus;
import gov.nysenate.ess.supply.item.model.ItemUnit;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.requisition.model.DeliveryMethod;
import gov.nysenate.ess.supply.requisition.model.PendingState;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.synchronization.dao.SfmsSynchronizationProcedure;
import gov.nysenate.ess.supply.synchronization.service.SfmsSynchronizationService;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class SfmsSynchronizationServiceTest {

    @Mock
    private RequisitionService requisitionService;
    @Mock
    private SfmsSynchronizationProcedure synchronizationProcedure;
    @Mock
    private DateTimeFactory dateTimeFactory;
    @Mock
    private SlackChatService slackChatService;

    private SfmsSynchronizationService service;

    @Before
    public void setup() {
        service = new SfmsSynchronizationService(
                true,
                requisitionService,
                synchronizationProcedure,
                dateTimeFactory,
                slackChatService
        );

        when(dateTimeFactory.now()).thenReturn(LocalDateTime.of(2020, 1, 1, 0, 0));
    }

    @Test
    public void givenSyncDisabled_thenSkipSync() {
        service = new SfmsSynchronizationService(
                false,
                requisitionService,
                synchronizationProcedure,
                dateTimeFactory,
                slackChatService
        );
        service.synchronizeRequisitions();
        verifyNoInteractions(requisitionService, synchronizationProcedure, slackChatService);
    }

    @Test
    public void givenLineItemsRequireSync_thenSyncAndMarkSaved() {
        Requisition requisition = buildRequisition(1001, setOf(lineItem(1, true)));
        when(requisitionService.searchRequisitions(any()))
                .thenReturn(new PaginatedList<>(1, LimitOffset.ALL, Collections.singletonList(requisition)));

        service.synchronizeRequisitions();

        verify(synchronizationProcedure).synchronizeRequisition(anyString());
        verify(requisitionService).savedInSfms(requisition.getRequisitionId(), true);
        verify(slackChatService, never()).sendMessage(anyString());
    }

    @Test
    public void givenSfmsError_thenSendSlackMsgAndDoNotMarkSaved() {
        Requisition requisition = buildRequisition(1002, setOf(lineItem(1, true)));
        when(requisitionService.searchRequisitions(any()))
                .thenReturn(new PaginatedList<>(1, LimitOffset.ALL, Collections.singletonList(requisition)));
        doThrow(new DataAccessResourceFailureException("db down"))
                .when(synchronizationProcedure)
                .synchronizeRequisition(anyString());

        service.synchronizeRequisitions();

        ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        verify(slackChatService).sendMessage(msgCaptor.capture());
        assertTrue(msgCaptor.getValue().contains("Error synchronizing requisition"));
        verify(requisitionService, never()).savedInSfms(requisition.getRequisitionId(), true);
    }

    @Test
    public void givenNoItemsRequireSync_thenDoNotSyncAndMarkSaved() {
        Set<LineItem> items = setOf(
                lineItem(0, true),
                lineItem(2, false)
        );
        Requisition requisition = buildRequisition(1003, items);
        when(requisitionService.searchRequisitions(any()))
                .thenReturn(new PaginatedList<>(1, LimitOffset.ALL, Collections.singletonList(requisition)));

        service.synchronizeRequisitions();

        verify(synchronizationProcedure, never()).synchronizeRequisition(anyString());
        verify(requisitionService).savedInSfms(requisition.getRequisitionId(), true);
    }

    @Test
    public void givenMixedSyncItems_thenSyncOnlyRequiredItems() {
        Requisition requisition = buildRequisition(1004, setOf(lineItem(1, true), lineItem(1, false)));
        when(requisitionService.searchRequisitions(any()))
                .thenReturn(new PaginatedList<>(1, LimitOffset.ALL, Collections.singletonList(requisition)));

        service.synchronizeRequisitions();

        ArgumentCaptor<String> xmlCaptor = ArgumentCaptor.forClass(String.class);
        verify(synchronizationProcedure).synchronizeRequisition(xmlCaptor.capture());
        String xml = xmlCaptor.getValue();
        assertTrue(xml.contains("<itemId>1</itemId>"));
        assertTrue(!xml.contains("<itemId>2</itemId>"));
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
                .withState(new PendingState())
                .withIssuer(issuer)
                .withModifiedBy(customer)
                .withModifiedDateTime(now)
                .withOrderedDateTime(now)
                .withApprovedDateTime(now.plusHours(1))
                .build();
    }

    private static LineItem lineItem(int quantity, boolean requiresSync) {
        ItemStatus status = requiresSync
                ? new ItemStatus(true, false, true, false)
                : new ItemStatus(true, true, true, false);
        SupplyItem item = new SupplyItem.Builder()
                .withId(requiresSync ? 1 : 2)
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
