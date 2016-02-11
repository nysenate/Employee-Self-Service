package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderSearchService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * Tests for the Order and OrderQuery services.
 */
public class OrderTests extends SupplyTests {

    private static int EMP_ID = 2;

    @Autowired private OrderService orderService;

    @Autowired private OrderSearchService searchService;

    @Before
    public void setUp() {
        resetInMemoryDaos();
    }

    @Test
    public void newOrderInitializedCorrectly() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        order = searchService.getOrderById(order.getId());
        assertTrue(order.getId() > 0);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canUpdateOrderLineItems() {
        Order originalOrder = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        Set<LineItem> newItems = incrementItemQuantities(originalOrder.getLineItems());
        orderService.updateOrderLineItems(originalOrder.getId(), newItems, MODIFIED_EMP_ID);
        Order updatedOrder = searchService.getOrderById(originalOrder.getId());
        assertNotEquals(originalOrder, updatedOrder);
        assertThat(updatedOrder.getLineItems(), is(equalTo(newItems)));
    }

    @Test
    public void orderProcessedCorrectly() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        orderService.processOrder(order.getId(), EMP_ID, MODIFIED_EMP_ID);
        order = searchService.getOrderById(order.getId());
        assertEquals(order.getIssuingEmployee().get().getEmployeeId(), EMP_ID);
        assertDateLessThan5SecondsOld(order.getProcessedDateTime().get());
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        Order order = createProcessingOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.processOrder(order.getId(), EMP_ID, MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        Order order = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.processOrder(order.getId(), EMP_ID, MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        order = orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), EMP_ID, MODIFIED_EMP_ID);
    }

    @Test
    public void orderCompletedCorrectly() {
        Order order = createProcessingOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
        order = searchService.getOrderById(order.getId());
        assertDateLessThan5SecondsOld(order.getCompletedDateTime().get());
        assertEquals(order.getStatus(), OrderStatus.COMPLETED);
    }

    @Test
    public void completingOrderUpdatesSfms() {
        Order order = createProcessingOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        assertThat(searchService.getSfmsOrders(ONE_WEEK_RANGE, LimitOffset.ALL).getResults().size(), is(0));
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
        assertThat(searchService.getSfmsOrders(ONE_WEEK_RANGE, LimitOffset.ALL).getResults().size(), is(1));
        // TODO: this can be improved if/when getSfmsOrderById is implemented.
    }

    @Test
    public void canUndoACompletion() {
        Order order = createProcessingOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.undoCompletion(order.getId(), MODIFIED_EMP_ID);
        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
        assertEquals(order.getCompletedDateTime().isPresent(), false);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        Order order = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        order = orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test
    public void canRejectPendingOrder() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void canRejectProcessingOrder() {
        Order order = createProcessingOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectCompletedOrder() {
        Order order = createCompletedOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID, ISSUING_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectRejectedOrder() {
        Order order = submitNewOrder(PENCILS_LGCLIPS_PAPERCLIPS, CUSTOMER_EMP_ID);
        order = orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
    }

    private Set<LineItem> incrementItemQuantities(Set<LineItem> originalItems) {
        Set<LineItem> newItems = new HashSet<>();
        for (LineItem lineItem : originalItems) {
            newItems.add(new LineItem(lineItem.getItem(), lineItem.getQuantity() + 1));
        }
        return newItems;
    }

    private void assertDateLessThan5SecondsOld(LocalDateTime orderDateTime) {
        assertTrue(orderDateTime.toInstant(ZoneOffset.UTC).isAfter(LocalDateTime.now().minusSeconds(5).toInstant(ZoneOffset.UTC)));
    }
}
