package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderSearchService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Transactional
@TransactionConfiguration(transactionManager = "localTxManager", defaultRollback = true)
public class OrderServiceTests extends SupplyTests {

    @Autowired private OrderService orderService;
    @Autowired private OrderSearchService searchService;
    private Order order;

    @Before
    public void setUp() {
        order = orderService.submitOrder(TestUtils.PENCILS_AND_PENS, TestUtils.CUSTOMER_EMP_ID, TestUtils.MODIFIED_EMP_ID);
    }

    @Test
    public void canSubmitNewOrder() {
        order = searchService.getOrderById(order.getId());
        assertTrue(order.getId() > 0);
        assertThat(order.getCustomer().getEmployeeId(), is(TestUtils.CUSTOMER_EMP_ID));
        assertEquals(order.getStatus(), OrderStatus.PENDING);
        assertThat(order.getOrderDateTime(), is(notNullValue()));
        assertThat(order.getModifiedDateTime(), is(notNullValue()));
        assertThat(order.getModifiedEmpId(), is(TestUtils.MODIFIED_EMP_ID));
    }

    // TODO: should items be deletable from an order?
    @Test
    public void canUpdateOrderLineItems() {
        Set<LineItem> newLineItems = incrementItemQuantities(order.getLineItems());

        orderService.updateOrderLineItems(order.getId(), newLineItems, TestUtils.MODIFIED_EMP_ID);

        Order updatedOrder = searchService.getOrderById(order.getId());
        assertThat(order, not(equalTo(updatedOrder)));
        assertThat(updatedOrder.getLineItems(), is(equalTo(newLineItems)));
    }

    @Test
    public void orderProcessedCorrectly() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.ALTERNATE_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertThat(order.getStatus(), is(OrderStatus.PROCESSING));
        assertThat(order.getIssuingEmployee().get().getEmployeeId(), is(TestUtils.ISSUING_EMP_ID));
        assertThat(order.getProcessedDateTime().isPresent(), is(true));
        assertThat(order.getModifiedEmpId(), is(TestUtils.ALTERNATE_EMP_ID));
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.ALTERNATE_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
    }

    @Test
    public void orderCompletedCorrectly() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);

        orderService.completeOrder(order.getId(), TestUtils.ALTERNATE_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertThat(order.getStatus(), is(OrderStatus.COMPLETED));
        assertThat(order.getCompletedDateTime().isPresent(), is(true));
        assertThat(order.getModifiedEmpId(), is(TestUtils.ALTERNATE_EMP_ID));
    }

    @Test
    public void canUndoACompletion() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);

        orderService.undoCompletion(order.getId(), TestUtils.ALTERNATE_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertThat(order.getStatus(), is(OrderStatus.PROCESSING));
        assertEquals(order.getCompletedDateTime().isPresent(), false);
        assertThat(order.getModifiedEmpId(), is(TestUtils.ALTERNATE_EMP_ID));
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
    }

    @Test
    public void canRejectPendingOrder() {
        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }


    // TODO: this need to be fleshed out some more. e.g. if order has issuing employee, should they be removed?
    @Test
    public void canRejectProcessingOrder() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);

        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectCompletedOrder() {
        orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectRejectedOrder() {
        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
    }

    @Test
    public void modifiedEmpAndModifiedDateTimeValuesGetSet() {
        assertThat(order.getModifiedEmpId(), is(notNullValue()));
        assertThat(order.getModifiedDateTime(), is(notNullValue()));

        Order processedOrder = orderService.processOrder(order.getId(), TestUtils.ISSUING_EMP_ID, TestUtils.MODIFIED_EMP_ID);
        assertThat(processedOrder.getModifiedEmpId(), equalTo(TestUtils.MODIFIED_EMP_ID));
        assertTrue(processedOrder.getModifiedDateTime().isAfter(order.getModifiedDateTime()));

        Order updatedItems = orderService.updateOrderLineItems(order.getId(), incrementItemQuantities(order.getLineItems()), TestUtils.ALTERNATE_EMP_ID);
        assertThat(updatedItems.getModifiedEmpId(), equalTo(TestUtils.ALTERNATE_EMP_ID));
        assertTrue(updatedItems.getModifiedDateTime().isAfter(processedOrder.getModifiedDateTime()));

        Order completedOrder = orderService.completeOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        assertThat(completedOrder.getModifiedEmpId(), equalTo(TestUtils.MODIFIED_EMP_ID));
        assertTrue(completedOrder.getModifiedDateTime().isAfter(updatedItems.getModifiedDateTime()));

        Order undoOrder = orderService.undoCompletion(order.getId(), TestUtils.ALTERNATE_EMP_ID);
        assertThat(undoOrder.getModifiedEmpId(), equalTo(TestUtils.ALTERNATE_EMP_ID));
        assertTrue(undoOrder.getModifiedDateTime().isAfter(completedOrder.getModifiedDateTime()));

        Order rejectOrder = orderService.rejectOrder(order.getId(), TestUtils.MODIFIED_EMP_ID);
        assertThat(rejectOrder.getModifiedEmpId(), equalTo(TestUtils.MODIFIED_EMP_ID));
        assertTrue(rejectOrder.getModifiedDateTime().isAfter(undoOrder.getModifiedDateTime()));
    }

    private Set<LineItem> incrementItemQuantities(Set<LineItem> originalItems) {
        Set<LineItem> newItems = new HashSet<>();
        for (LineItem lineItem : originalItems) {
            newItems.add(new LineItem(lineItem.getItem(), lineItem.getQuantity() + 1));
        }
        return newItems;
    }
}
