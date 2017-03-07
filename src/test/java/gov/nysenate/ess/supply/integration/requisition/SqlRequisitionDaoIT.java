package gov.nysenate.ess.supply.integration.requisition;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.unit.fixtures.RequisitionFixture;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class SqlRequisitionDaoIT extends BaseTest {

    @Autowired private RequisitionDao requisitionDao;

    @Test
    public void canInsertRequisition() {
        requisitionDao.saveRequisition(RequisitionFixture.getPendingRequisition());
    }

    @Test
    public void canGetRequisition() {
        Requisition requisition = requisitionDao.getRequisitionById(2).get();
        assertTrue(requisition.getRequisitionId() == 2);
    }

    @Test
    public void canSearchRequisitions() {
        Range<LocalDateTime> dateRange = Range.closed(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        requisitionDao.searchRequisitions("A42FB", "any", EnumSet.allOf(RequisitionStatus.class),
                dateRange, "ordered_date_time", "any", LimitOffset.ALL, "All");
    }

    @Test
    public void canSearchOrderHistory() {
        Range<LocalDateTime> dateRange = Range.closed(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        requisitionDao.searchOrderHistory("A42FB", 1, EnumSet.allOf(RequisitionStatus.class),
                                          dateRange, "ordered_date_time", LimitOffset.ALL);
    }

}



//    @Before
//    public void setup() {
//        LocalDateTime createdDateTime = LocalDateTime.now();
//        requisition = new Requisition(createdDateTime, RequisitionFixture.getPendingVersion());
//    }
//
//    @Test
//    public void canInsertRequisition() {
//        int id = requisitionDao.saveRequisition(requisition);
//        assertThat(id, is(greaterThan(0)));
//    }
//
//    // TODO: can update requisition
//
//    @Test
//    public void canGetRequisitionById() {
//        int id = requisitionDao.saveRequisition(requisition);
//        Requisition actual = requisitionDao.getRequisitionById(id);
//        // only tests that the sql executes without errors
//        // cant do much testing unless we create versions with real employees
//    }
//
//    @Test
//    public void canSearchRequisitions() {
//        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);
//        LocalDateTime toDate = LocalDateTime.now();
//        Range<LocalDateTime> dateRange = Range.closed(fromDate, toDate);
//        PaginatedList<Requisition> requisitions = requisitionDao.searchRequisitions("all", "all", EnumSet.allOf(RequisitionStatus.class),
//                                                                                    dateRange, "processed_date_time", LimitOffset.ALL);
//        assertThat(requisitions.getResults().size(), is(greaterThan(1)));
//    }
//
//    @Test
//    public void canSearchOrderHistory() {
//        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);
//        LocalDateTime toDate = LocalDateTime.now();
//        Range<LocalDateTime> dateRange = Range.closed(fromDate, toDate);
//        PaginatedList<Requisition> requisitions = requisitionDao.searchOrderHistory("A42FB-W", 11168, EnumSet.allOf(RequisitionStatus.class),
//                                                                                    dateRange, "ordered_date_time", LimitOffset.ALL);
//        assertThat(requisitions.getResults().size(), is(greaterThan(1)));
//    }
//}
