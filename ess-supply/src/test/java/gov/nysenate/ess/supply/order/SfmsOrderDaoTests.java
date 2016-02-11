package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.sfms.SfmsLineItem;
import gov.nysenate.ess.supply.sfms.SfmsOrder;
import gov.nysenate.ess.supply.sfms.SfmsOrderId;
import gov.nysenate.ess.supply.sfms.dao.EssSfmsOrderDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@Transactional
@TransactionConfiguration(transactionManager = "remoteTxManager", defaultRollback = true)
public class SfmsOrderDaoTests extends SupplyTests {

    @Autowired
    private EssSfmsOrderDao orderDao;

    private static final String SUPPLY_LOC_CODE = "LC100S";
    private static final String SUPPLY_LOC_TYPE = "P";

    @Test
    public void canGetSfmsOrders() {
        List<SfmsOrder> actualOrders = orderDao.getOrders("all", "all", "all", SIX_MONTH_RANGE, LimitOffset.TEN).getResults();
        assertThat(actualOrders.size(), greaterThan(0));
        assertOnlySupplyOrdersRetrieved(actualOrders);
    }

    private void assertOnlySupplyOrdersRetrieved(List<SfmsOrder> actualOrders) {
        for (SfmsOrder order: actualOrders) {
            assertThat(order.getFromLocationCode(), is(SUPPLY_LOC_CODE));
            assertThat(order.getFromLocationType(), is(SUPPLY_LOC_TYPE));
        }
    }

    @Test
    public void canGetSfmsOrderById() {
        Order expected = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderDao.saveOrder(expected);
        SfmsOrder actual = orderDao.getOrderById(extractSfmsOrderIdFromOrder(expected));
        assertSfmsOrderEqualsOrder(actual, expected);
    }

    @Test
    public void canGetSfmsOrdersByLocation() {
        int originalSize = orderDao.getOrders("A42FB", "W", "all", ONE_WEEK_RANGE, LimitOffset.ALL).getResults().size();
        Order order = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderDao.saveOrder(order);
        int actualSize = orderDao.getOrders("A42FB", "W", "all", ONE_WEEK_RANGE, LimitOffset.ALL).getResults().size();
        assertThat(actualSize, is(originalSize + 1));
    }

    @Test
    public void canGetSfmsOrdersByIssuer() {
        int originalSize = orderDao.getOrders("all", "all", "CASEIRAS", ONE_WEEK_RANGE, LimitOffset.ALL).getResults().size();
        Order order = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderDao.saveOrder(order);
        int actualSize = orderDao.getOrders("all", "all", "CASEIRAS", ONE_WEEK_RANGE, LimitOffset.ALL).getResults().size();
        assertThat(actualSize, is(originalSize + 1));
    }

    @Test
    public void canSaveSfmsOrder() {
        Order expected = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderDao.saveOrder(expected);
        SfmsOrder actual = orderDao.getOrderById(extractSfmsOrderIdFromOrder(expected));
        assertSfmsOrderEqualsOrder(actual, expected);
    }

    private SfmsOrderId extractSfmsOrderIdFromOrder(Order order) {
        // Until Order dao is set up, just use 1 for nuissue.
        return new SfmsOrderId(1, order.getCompletedDateTime().get().toLocalDate(),
                               order.getLocationCode(), order.getLocationType());
    }

    private void assertSfmsOrderEqualsOrder(SfmsOrder sfmsOrder, Order order) {
        assertThat(sfmsOrder.getOrderId(), equalTo(extractSfmsOrderIdFromOrder(order)));
        assertThat(sfmsOrder.getFromLocationCode(), is("LC100S"));
        assertThat(sfmsOrder.getFromLocationType(), is("P"));
        assertThat(sfmsOrder.getIssuedBy(), is(order.getIssuingEmployee().get().getLastName().toUpperCase()));
        assertThat(sfmsOrder.getResponsibilityCenterHead(), is("STSBAC"));
        assertSfmsLineItemsEqualOrderLineItems(sfmsOrder, order);
    }

    private void assertSfmsLineItemsEqualOrderLineItems(SfmsOrder sfmsOrder, Order order) {
        for (SfmsLineItem sfmsLineItem: sfmsOrder.getItems()) {
            LineItem orderLineItem = getLineItemWithId(order.getLineItems(), sfmsLineItem.getItemId());
            if (orderLineItem == null) {
                fail("Order does not have a LineItem with id " + sfmsLineItem.getItemId());
            }
            assertThat(sfmsLineItem.getItemId(), is(orderLineItem.getItem().getId()));
            assertThat(sfmsLineItem.getQuantity(), is(orderLineItem.getQuantity()));
            assertThat(sfmsLineItem.getUnit(), is(orderLineItem.getItem().getUnit()));
            assertThat(sfmsLineItem.getStandardQuantity(),
                       is(orderLineItem.getQuantity() * orderLineItem.getItem().getUnitStandardQuantity()));
        }
    }

    private LineItem getLineItemWithId(Set<LineItem> lineItems, int id) {
        for (LineItem lineItem: lineItems) {
            if(lineItem.getItem().getId() == id) {
                return lineItem;
            }
        }
        return null;
    }
}
