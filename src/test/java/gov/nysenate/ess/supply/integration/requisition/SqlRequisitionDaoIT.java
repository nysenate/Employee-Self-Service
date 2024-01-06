package gov.nysenate.ess.supply.integration.requisition;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.unit.fixtures.RequisitionFixture;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Ignore
@Category(IntegrationTest.class)
@Transactional(value = "remoteTxManager")
@Rollback
public class SqlRequisitionDaoIT extends BaseTest {

    @Autowired private RequisitionDao requisitionDao;

    @Test
    public void canInsertRequisition() {
        requisitionDao.saveRequisition(RequisitionFixture.getPendingRequisition());
    }

    @Test
    public void canGetRequisition() {
        Requisition requisition = requisitionDao.getRequisitionById(2).get();
        assertEquals(2, requisition.getRequisitionId());
    }

    @Test
    public void canSearchRequisitions() {
        RequisitionQuery query = new RequisitionQuery().setDestination("A42FB").setLimitOffset(LimitOffset.ALL);
        requisitionDao.searchRequisitions(query);
    }

    @Test
    public void canSearchOrderHistory() {
        RequisitionQuery query = new RequisitionQuery()
                .setDestination("A42FB")
                .setCustomerId(1)
                .setLimitOffset(LimitOffset.ALL);
        requisitionDao.searchOrderHistory(query);
    }

}
