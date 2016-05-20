package gov.nysenate.ess.supply.unit.requisition;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.Category;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.dao.SqlRequisitionDao;
import gov.nysenate.ess.supply.requisition.service.RequisitionUpdateService;
import gov.nysenate.ess.supply.requisition.service.SupplyRequisitionUpdateService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class RequisitionUpdateServiceTests extends SupplyTests {

    @Mock private RequisitionDao requisitionDao = new SqlRequisitionDao();
    @InjectMocks private RequisitionUpdateService updateService = new SupplyRequisitionUpdateService();

    private static final LocalDateTime orderedDateTime = LocalDateTime.now();
    private RequisitionVersion pendingVersion;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pendingVersion = RequisitionFixture.getPendingVersion();
    }

    @Test
    public void canCreateRequisition() {
        updateService.submitNewRequisition(orderedDateTime, pendingVersion);
        Mockito.verify(requisitionDao).insertRequisition(new Requisition(orderedDateTime, pendingVersion));
    }

    // can insert new requisition
    // can add version to requisition
    // can reject/approve?
}
