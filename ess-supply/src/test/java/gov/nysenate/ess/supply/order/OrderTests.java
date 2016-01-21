package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.UnitTestUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.dao.sfms.SfmsOrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderService;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class OrderTests extends SupplyTests {

    private static int EMP_ID = 2;

    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() {
        UnitTestUtils.resetInMemoryDaos();
    }

    @Test
    public void newOrderInitializedCorrectly() {
        Order order = UnitTestUtils.submitOrder();
        assertTrue(order.getId() > 0);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canEditAnOrdersItems() {
        Order order = UnitTestUtils.submitOrder();
        Set<LineItem> newItems = incrementItemQuantities(order.getItems());
        orderService.saveOrder(order.setItems(newItems));
        assertNotEquals(order, orderService.getOrderById(order.getId()));
    }

    @Test
    public void orderProcessedCorrectly() {
        Order order = UnitTestUtils.submitOrder();
        order = orderService.processOrder(order.getId(), EMP_ID);
        assertEquals(order.getIssuingEmployee().getEmployeeId(), EMP_ID);
        assertDateLessThan5SecondsOld(order.getProcessedDateTime());
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        Order order = UnitTestUtils.submitAndProcessOrder();
        orderService.processOrder(order.getId(), EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        Order order = UnitTestUtils.submitProcessAndCompleteOrder();
        orderService.processOrder(order.getId(), EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        Order order = UnitTestUtils.submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.processOrder(order.getId(), EMP_ID);
    }

    @Test
    public void orderCompletedCorrectly() {
        Order order = UnitTestUtils.submitProcessAndCompleteOrder();
        assertDateLessThan5SecondsOld(order.getCompletedDateTime());
        assertEquals(order.getStatus(), OrderStatus.COMPLETED);
    }

    @Test
    public void completingOrderUpdatesSfms() {
        Order order = UnitTestUtils.submitAndProcessOrder();
        assertThat(orderService.getSfmsOrders(UnitTestUtils.getDateRange(), LimitOffset.ALL).size(), is(0));
        orderService.completeOrder(order.getId());
        assertThat(orderService.getSfmsOrders(UnitTestUtils.getDateRange(), LimitOffset.ALL).size(), is(1));
    }

    // TODO: May not keep this functionality.
    @Ignore
    @Test
    public void canUndoACompletion() {
        Order order = UnitTestUtils.submitProcessAndCompleteOrder();
        order = orderService.undoCompletion(order.getId());
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
        assertEquals(order.getCompletedDateTime(), null);
        // TODO: should also check sfms for order.
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        Order order = UnitTestUtils.submitOrder();
        orderService.completeOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        Order order = UnitTestUtils.submitProcessAndCompleteOrder();
        orderService.completeOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        Order order = UnitTestUtils.submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.completeOrder(order.getId());
    }

    @Test
    public void canRejectPendingOrder() {
        Order order = UnitTestUtils.submitOrder();
        assertEquals(order.getStatus(), OrderStatus.PENDING);
        order = orderService.rejectOrder(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void canRejectProcessingOrder() {
        Order order = UnitTestUtils.submitAndProcessOrder();
        order = orderService.rejectOrder(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectCompletedOrder() {
        Order order = UnitTestUtils.submitProcessAndCompleteOrder();
        orderService.rejectOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectRejectedOrder() {
        Order order = UnitTestUtils.submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.rejectOrder(order.getId());
    }

    private Set<LineItem> incrementItemQuantities(Set<LineItem> originalItems) {
        Set<LineItem> newItems = new HashSet<>();
        for (LineItem lineItem : originalItems) {
            newItems.add(new LineItem(lineItem.getItemId(), lineItem.getQuantity() + 1));
        }
        return newItems;
    }

    private void assertDateLessThan5SecondsOld(LocalDateTime orderDateTime) {
        assertTrue(orderDateTime.toInstant(ZoneOffset.UTC).isAfter(LocalDateTime.now().minusSeconds(5).toInstant(ZoneOffset.UTC)));
    }
}
