package gov.nysenate.ess.supply.unit.requisition;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.dao.SqlRequisitionDao;
import gov.nysenate.ess.supply.requisition.service.RequisitionService;
import gov.nysenate.ess.supply.requisition.service.SupplyRequisitionService;
import gov.nysenate.ess.supply.util.date.DateTimeFactory;
import gov.nysenate.ess.supply.util.date.SupplyDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

public class RequisitionUpdateServiceTests extends SupplyTests {

    @Mock private RequisitionDao requisitionDao = new SqlRequisitionDao();
    @Mock private DateTimeFactory dateTimeFactory = new SupplyDateTime();
    @InjectMocks private RequisitionService updateService = new SupplyRequisitionService();

    private static int requisitionId;
    private RequisitionVersion pendingVersion;
    private RequisitionVersion processingVersion;
    private RequisitionVersion rejectedVersion;
    private static LocalDateTime orderedDateTime;
    private static LocalDateTime processedDateTime;
    private static LocalDateTime rejectedDateTime;

    @BeforeClass
    public static void before() {
        requisitionId = 1;
        orderedDateTime = LocalDateTime.now();
        processedDateTime = orderedDateTime.plusMinutes(5);
        rejectedDateTime = processedDateTime.plusMinutes(5);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pendingVersion = RequisitionFixture.getPendingVersion();
        processingVersion = RequisitionFixture.getProcessingVersion();
        rejectedVersion = RequisitionFixture.getRejectedVersion();
        when(dateTimeFactory.now()).thenReturn(orderedDateTime);
    }

    @Test
    public void canSubmitNewRequisition() {
        updateService.submitNewRequisition(pendingVersion);
        Mockito.verify(requisitionDao).saveRequisition(pendingRequisition());
    }

    @Test(expected = IllegalArgumentException.class)
    public void submittedRequisitionMustHavePendingStatus() {
        updateService.submitNewRequisition(processingVersion);
    }

    @Test
    public void canUpdateRequisition() {
        when(dateTimeFactory.now()).thenReturn(processedDateTime);
        when(requisitionDao.getRequisitionById(requisitionId)).thenReturn(pendingRequisition());
        updateService.updateRequisition(requisitionId, processingVersion);
        Mockito.verify(requisitionDao).saveRequisition(processingRequisition());
    }

    @Test
    public void canUndoRequisitionRejection() {
        LocalDateTime undoDateTime = rejectedDateTime.plusMinutes(5);
        when(dateTimeFactory.now()).thenReturn(undoDateTime);
        when(requisitionDao.getRequisitionById(requisitionId)).thenReturn(rejectedRequisition());
        Requisition expected = rejectedRequisition();
        expected.addVersion(undoDateTime, processingVersion);

        updateService.undoRejection(requisitionId);
        Mockito.verify(requisitionDao).saveRequisition(expected);
    }

    private Requisition pendingRequisition() {
        return new Requisition(orderedDateTime, pendingVersion);
    }

    private Requisition processingRequisition() {
        Requisition processingRequisition = pendingRequisition();
        processingRequisition.addVersion(processedDateTime, processingVersion);
        return  processingRequisition;
    }

    private Requisition rejectedRequisition() {
        Requisition requisition = processingRequisition();
        requisition.addVersion(rejectedDateTime, rejectedVersion);
        return requisition;
    }
}
