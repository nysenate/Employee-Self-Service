package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.order.dao.SqlOrderDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class OrderDaoTests extends SupplyTests {

    @Autowired SqlOrderDao orderDao;

    @Test
    public void canGetOrderById() {

    }

    @Test
    public void canInsertNewOrder() {
        Order order = createPendingOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        orderDao.insertOrder(order, LocalDateTime.now());
    }
}
