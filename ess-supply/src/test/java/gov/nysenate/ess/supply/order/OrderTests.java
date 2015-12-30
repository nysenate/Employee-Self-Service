package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.TestUtils;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.order.dao.OrderDao;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.Assert.*;

public class OrderTests extends SupplyTests {

    @Autowired
    private OrderService orderService;

    @Qualifier("sfmsInMemoryOrder")
    @Autowired
    private OrderDao sfmsDao;

    @Before
    public void setUp() {
        TestUtils.resetInMemoryDaos();
    }

    @Test
    public void newOrderInitializedCorrectly() {
        Order order = TestUtils.submitOrder();
        assertTrue(order.getId() > 0);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }

    @Test
    public void canEditAnOrdersItems() {
        Order order = TestUtils.submitOrder();
        Set<LineItem> newItems = incrementItemQuantities(order.getItems());
        orderService.saveOrder(order.setItems(newItems));
        assertNotEquals(order, orderService.getOrderById(order.getId()));
    }

    @Test
    public void orderProcessedCorrectly() {
        Order order = TestUtils.submitOrder();
        order = orderService.processOrder(order.getId(), 2);
        assertEquals(order.getIssuingEmployee().getEmployeeId(), 2);
        assertDateLessThan5SecondsOld(order.getProcessedDateTime());
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        Order order = TestUtils.submitAndProcessOrder();
        orderService.processOrder(order.getId(), 2);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        Order order = TestUtils.submitProcessAndCompleteOrder();
        orderService.processOrder(order.getId(), 2);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        Order order = TestUtils.submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.processOrder(order.getId(), 2);
    }

    @Test
    public void orderCompletedCorrectly() {
        Order order = TestUtils.submitProcessAndCompleteOrder();
        assertDateLessThan5SecondsOld(order.getCompletedDateTime());
        assertEquals(order.getStatus(), OrderStatus.COMPLETED);
    }

    @Test
    public void completingOrderUpdatesSfms() {
        Order order = TestUtils.submitAndProcessOrder();
        assertTrue(sfmsDao.getOrders().size() == 0);
        orderService.completeOrder(order.getId());
        assertTrue(sfmsDao.getOrders().size() == 1);
    }

    @Test
    public void canUndoACompletion() {
        Order order = TestUtils.submitProcessAndCompleteOrder();
        order = orderService.undoCompletion(order.getId());
        assertEquals(order.getStatus(), OrderStatus.PROCESSING);
        assertEquals(order.getCompletedDateTime(), null);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        Order order = TestUtils.submitOrder();
        orderService.completeOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        Order order = TestUtils.submitProcessAndCompleteOrder();
        orderService.completeOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        Order order = TestUtils.submitOrder();
        order = orderService.rejectOrder(order.getId());
        orderService.completeOrder(order.getId());
    }

    @Test
    public void canRejectPendingOrder() {
        Order order = TestUtils.submitOrder();
        assertEquals(order.getStatus(), OrderStatus.PENDING);
        order = orderService.rejectOrder(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void canRejectProcessingOrder() {
        Order order = TestUtils.submitAndProcessOrder();
        order = orderService.rejectOrder(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectCompletedOrder() {
        Order order = TestUtils.submitProcessAndCompleteOrder();
        orderService.rejectOrder(order.getId());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectRejectedOrder() {
        Order order = TestUtils.submitOrder();
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
