package gov.nysenate.ess.supply.unit.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.dao.InMemorySupplyItemDao;
import gov.nysenate.ess.supply.item.dao.SupplyItemDao;
import gov.nysenate.ess.supply.order.*;
import gov.nysenate.ess.supply.order.dao.InMemoryOrderDao;
import gov.nysenate.ess.supply.shipment.SupplyShipmentService;
import gov.nysenate.ess.supply.util.date.DummyDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class OrderTests {

    private OrderService orderService;
    private InMemoryOrderDao orderDao;
    private DummyDateTime dummyDateTime;
    private static SupplyItemDao itemDao;

    private static int ORDER_ID;
    private static Employee CUSTOMER;
    private static Employee MODIFIED_BY;
    private static Location DESTINATION;
    private static LocalDateTime SUBMITTED_DATE_TIME;
    private static LocalDateTime MODIFIED_DATE_TIME;
    private static Set<LineItem> ORIGINAL_LINE_ITEMS;

    private Order order;
    private OrderVersion firstVersion;

    @BeforeClass
    public static void before() {
        CUSTOMER = new Employee();
        CUSTOMER.setEmployeeId(1);
        MODIFIED_BY = new Employee();
        MODIFIED_BY.setEmployeeId(2);
        DESTINATION = new Location(new LocationId("A42FB", 'W'), new Address(), new ResponsibilityHead());
        itemDao = new InMemorySupplyItemDao();
        ORIGINAL_LINE_ITEMS = new HashSet<>();
        ORIGINAL_LINE_ITEMS.add(new LineItem(itemDao.getItemById(1), 3));
        ORIGINAL_LINE_ITEMS.add(new LineItem(itemDao.getItemById(2), 3));

        SUBMITTED_DATE_TIME = LocalDateTime.now();
        MODIFIED_DATE_TIME = SUBMITTED_DATE_TIME.plusMinutes(5);
    }

    @Before
    public void setup() {
        /** Initialize dependencies */
        dummyDateTime = new DummyDateTime();
        orderDao = new InMemoryOrderDao();
        orderService = new SupplyOrderService(orderDao, dummyDateTime, null); // TODO fix this

        /** Submit first order version */
        dummyDateTime.setDateTime(SUBMITTED_DATE_TIME);
        firstVersion = new OrderVersion.Builder().withId(1).withCustomer(CUSTOMER).withDestination(DESTINATION)
                .withLineItems(ORIGINAL_LINE_ITEMS).withModifiedBy(CUSTOMER).withStatus(OrderStatus.APPROVED).build();
        ORDER_ID = orderService.submitOrder(firstVersion);
        order = orderService.getOrder(ORDER_ID);
        dummyDateTime.setDateTime(MODIFIED_DATE_TIME);
    }

    @Test
    public void canSubmitNewOrder() {
        assertNotNull("Order is saved", orderService.getOrder(ORDER_ID));
        assertEquals("Order contains submitted version", firstVersion, order.getHistory().get(SUBMITTED_DATE_TIME));
    }

    @Test
    public void canRejectOrder() {
        String note = "Reject note.";
        orderService.rejectOrder(order, note, MODIFIED_BY);
        order = orderService.getOrder(ORDER_ID);
        assertEquals("Note is saved.", note, order.getNote().get());
        assertEquals("Status set to rejected.", OrderStatus.REJECTED, order.getStatus());
        assertEquals("Modified employee updated", MODIFIED_BY, order.getModifiedBy());
        assertNotNull("Uses DateTimeFactory when setting modified date time.", order.getHistory().get(MODIFIED_DATE_TIME));
    }

    @Test
    public void canUpdateLineItems() {
        Set<LineItem> lineItemsIncreasedQuantities;
        lineItemsIncreasedQuantities = new HashSet<>();
        lineItemsIncreasedQuantities.add(new LineItem(itemDao.getItemById(1), 5));
        lineItemsIncreasedQuantities.add(new LineItem(itemDao.getItemById(2), 5));

        String note = "Increasing quantities";
        orderService.updateLineItems(order, lineItemsIncreasedQuantities, note, MODIFIED_BY);
        order = orderService.getOrder(order.getId());
        assertEquals("Line items updated", lineItemsIncreasedQuantities, order.getLineItems());
        assertNotNull("Uses DateTimeFactory when setting modified date time.", order.getHistory().get(MODIFIED_DATE_TIME));
    }

    // Adding new version - new version should have id = 0

    // TODO: can only adjust line item quantities, cannot add or remove line items.

    @Test
    public void canGetOrderedDateTimeWhenMultipleVersions() {
        orderService.rejectOrder(order, null, MODIFIED_BY);
        order = orderService.getOrder(ORDER_ID);
        assertEquals(SUBMITTED_DATE_TIME, order.getOrderedDateTime());
    }

    @Test
    public void oldVersionsAreSavedWhenOrderUpdated() {
        orderService.rejectOrder(order, null, MODIFIED_BY);
        assertEquals("Order history contains first version.", firstVersion, order.getHistory().get(SUBMITTED_DATE_TIME));
    }

    @Test
    public void orderGettersReturnValuesFromMostRecentVersion() {
        assertEquals(OrderStatus.APPROVED, order.getStatus());
        assertEquals(CUSTOMER, order.getModifiedBy());
        assertEquals(Optional.empty(), order.getNote());

        orderService.rejectOrder(order, "A note", MODIFIED_BY);
        order = orderService.getOrder(ORDER_ID);

        assertEquals(OrderStatus.REJECTED, order.getStatus());
        assertEquals(MODIFIED_BY, order.getModifiedBy());
        assertEquals(Optional.of("A note"), order.getNote());
    }
}
