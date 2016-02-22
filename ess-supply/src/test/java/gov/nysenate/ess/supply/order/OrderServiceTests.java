package gov.nysenate.ess.supply.order;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.supply.SupplyTests;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.SupplyItem;
import gov.nysenate.ess.supply.order.exception.WrongOrderStatusException;
import gov.nysenate.ess.supply.order.service.OrderSearchService;
import gov.nysenate.ess.supply.order.service.OrderService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sound.sampled.Line;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Tests for the Order and OrderQuery services.
 */
public class OrderServiceTests extends SupplyTests {

    private static int EMP_ID = 2;

    @Autowired private OrderService orderService;
    @Autowired private OrderSearchService searchService;

    protected static final int CUSTOMER_EMP_ID = 6221;
    protected static final int ISSUING_EMP_ID = 11168;
    protected static final int MODIFIED_EMP_ID = 10012;
    protected static final int ALTERNATE_EMP_ID = 7822;
    private static Set<LineItem> PENCILS_AND_PENS;

    @BeforeClass
    public static void init() {
        PENCILS_AND_PENS = new HashSet<>();
        PENCILS_AND_PENS.add(new LineItem(new SupplyItem(1, "P2", "Pencils", "Number 2 Yellow Pencils", "24/PKG", "Pencils", 1, 24), 1));
        PENCILS_AND_PENS.add(new LineItem(new SupplyItem(2, "PBL", "Blue Ballpoint Pens", "Blue ink, bold point", "DOZEN", "Pens", 1, 12), 2));
    }

    @Before
    public void setUp() {
    }

    @Test
    public void canSubmitNewOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertTrue(order.getId() > 0);
        assertThat(order.getCustomer().getEmployeeId(), is(CUSTOMER_EMP_ID));
        assertEquals(order.getStatus(), OrderStatus.PENDING);
        assertDateLessThan5SecondsOld(order.getOrderDateTime());
        assertDateLessThan5SecondsOld(order.getModifiedDateTime());
        assertThat(order.getModifiedEmpId(), is(MODIFIED_EMP_ID));
    }

    // TODO: should items be deletable from an order?
    @Test
    public void canUpdateOrderLineItems() {
        Order originalOrder = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        Set<LineItem> newLineItems = incrementItemQuantities(originalOrder.getLineItems());

        orderService.updateOrderLineItems(originalOrder.getId(), newLineItems, MODIFIED_EMP_ID);

        Order updatedOrder = searchService.getOrderById(originalOrder.getId());
        assertThat(originalOrder, not(equalTo(updatedOrder)));
        assertThat(updatedOrder.getLineItems(), is(equalTo(newLineItems)));
    }

    @Test
    public void orderProcessedCorrectly() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, ALTERNATE_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertThat(order.getStatus(), is(OrderStatus.PROCESSING));
        assertThat(order.getIssuingEmployee().get().getEmployeeId(), is(ISSUING_EMP_ID));
        assertDateLessThan5SecondsOld(order.getProcessedDateTime().get());
        assertThat(order.getModifiedEmpId(), is(ALTERNATE_EMP_ID));
        assertDateLessThan5SecondsOld(order.getModifiedDateTime());
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessAnAlreadyProcessingOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, ALTERNATE_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessACompletedOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cantProcessRejectedOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
    }

    @Test
    public void orderCompletedCorrectly() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);

        orderService.completeOrder(order.getId(), ALTERNATE_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertThat(order.getStatus(), is(OrderStatus.COMPLETED));
        assertDateLessThan5SecondsOld(order.getCompletedDateTime().get());
        assertThat(order.getModifiedEmpId(), is(ALTERNATE_EMP_ID));
        assertDateLessThan5SecondsOld(order.getModifiedDateTime());
    }

    @Test
    public void canUndoACompletion() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);

        orderService.undoCompletion(order.getId(), ALTERNATE_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertThat(order.getStatus(), is(OrderStatus.PROCESSING));
        assertEquals(order.getCompletedDateTime().isPresent(), false);
        assertDateLessThan5SecondsOld(order.getModifiedDateTime());
        assertThat(order.getModifiedEmpId(), is(ALTERNATE_EMP_ID));
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAPendingOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteAnAlreadyCompleteOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void cannotCompleteRejectedOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test
    public void canRejectPendingOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);

        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }


    // FIXME: this need to be fleshed out some more. e.g. if order has issuing employee, should they be removed?
    @Test
    public void canRejectProcessingOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);

        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);

        order = searchService.getOrderById(order.getId());
        assertEquals(order.getStatus(), OrderStatus.REJECTED);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectCompletedOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.processOrder(order.getId(), ISSUING_EMP_ID, MODIFIED_EMP_ID);
        orderService.completeOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
    }

    @Test(expected = WrongOrderStatusException.class)
    public void canNotRejectRejectedOrder() {
        Order order = orderService.submitOrder(PENCILS_AND_PENS, CUSTOMER_EMP_ID, MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
        orderService.rejectOrder(order.getId(), MODIFIED_EMP_ID);
    }

    private void assertDateLessThan5SecondsOld(LocalDateTime orderDateTime) {
        assertTrue(orderDateTime.toInstant(ZoneOffset.UTC).isAfter(LocalDateTime.now().minusSeconds(5).toInstant(ZoneOffset.UTC)));
    }

    private Set<LineItem> incrementItemQuantities(Set<LineItem> originalItems) {
        Set<LineItem> newItems = new HashSet<>();
        for (LineItem lineItem : originalItems) {
            newItems.add(new LineItem(lineItem.getItem(), lineItem.getQuantity() + 1));
        }
        return newItems;
    }
}
