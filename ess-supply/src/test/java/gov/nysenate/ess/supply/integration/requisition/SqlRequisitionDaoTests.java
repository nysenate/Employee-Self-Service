package gov.nysenate.ess.supply.integration.requisition;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.dao.SqlRequisitionDao;
import gov.nysenate.ess.supply.unit.requisition.RequisitionFixture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class SqlRequisitionDaoTests extends SupplyTests {

    @Autowired private SqlRequisitionDao requisitionDao;

    private Requisition requisition;

    @Before
    public void setup() {
        LocalDateTime createdDateTime = LocalDateTime.now();
        requisition = new Requisition(createdDateTime, RequisitionFixture.getPendingVersion());
    }

    @Test
    public void canInsertRequisition() {
        int id = requisitionDao.saveRequisition(requisition);
        assertThat(id, is(greaterThan(0)));
    }
}
