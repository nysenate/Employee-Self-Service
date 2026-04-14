package gov.nysenate.ess.supply.integration.requisition;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.annotation.SillyTest;
import gov.nysenate.ess.core.config.DatabaseConfig;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.requisition.dao.RequisitionDao;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import gov.nysenate.ess.supply.requisition.model.RequisitionQuery;
import gov.nysenate.ess.supply.unit.fixtures.RequisitionFixture;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

@Category(SillyTest.class)
@Transactional(value = DatabaseConfig.localTxManager)
@Rollback
public class SqlRequisitionDaoIT extends BaseTest {

    @Autowired private RequisitionDao requisitionDao;
    @Resource(name = "localJdbcTemplate") private JdbcTemplate localJdbcTemplate;

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

    @AfterTransaction
    public void resetSupplyRequisitionSequences() {
        resetSequence("supply.requisition_requisition_id_seq", "supply.requisition", "requisition_id");
        resetSequence("supply.requisition_content_revision_id_seq", "supply.requisition_content", "revision_id");
    }

    private void resetSequence(String sequenceName, String tableName, String idColumnName) {
        Integer maxId = localJdbcTemplate.queryForObject(
                "SELECT max(" + idColumnName + ") FROM " + tableName,
                Integer.class
        );
        if (maxId == null) {
            localJdbcTemplate.queryForObject(
                    "SELECT setval('" + sequenceName + "'::regclass, 1, false)",
                    Long.class
            );
            return;
        }
        localJdbcTemplate.queryForObject(
                "SELECT setval('" + sequenceName + "'::regclass, ?, true)",
                Long.class,
                maxId
        );
    }

}
