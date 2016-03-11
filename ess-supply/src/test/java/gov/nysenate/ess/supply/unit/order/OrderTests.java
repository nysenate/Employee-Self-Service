package gov.nysenate.ess.supply.unit.order;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.supply.order.*;
import gov.nysenate.ess.supply.order.dao.InMemoryOrderDao;
import gov.nysenate.ess.supply.util.date.DummyDateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.Assert.*;

public class OrderTests {

    private OrderService orderService;
    private InMemoryOrderDao orderDao;
    private DummyDateTime dummyDateTime;

    private static Employee CUSTOMER;
    private static Employee MODIFIED_BY;
    private static Location DESTINATION;
    private static LocalDateTime ORDER_SUBMITTED_DATE_TIME;
    private static LocalDateTime ORDER_MODIFIED_DATE_TIME;

    private Order order;
    private OrderVersion firstVersion;

    @BeforeClass
    public static void before() {
        CUSTOMER = new Employee();
        CUSTOMER.setEmployeeId(1);
        MODIFIED_BY = new Employee();
        MODIFIED_BY.setEmployeeId(2);
        DESTINATION = new Location();
        DESTINATION.setCode("A42FB");
        DESTINATION.setType(LocationType.valueOfCode('W'));

        ORDER_SUBMITTED_DATE_TIME = LocalDateTime.now();
        ORDER_MODIFIED_DATE_TIME = ORDER_SUBMITTED_DATE_TIME.plusMinutes(5);
    }

    @Before
    public void setup() {
        /** Initialize dependencies */
        dummyDateTime = new DummyDateTime();
        dummyDateTime.dateTime = ORDER_SUBMITTED_DATE_TIME;
        orderDao = new InMemoryOrderDao();
        orderService = new SupplyOrderService(orderDao, dummyDateTime);

        /** Submit first order version */
        firstVersion = new OrderVersion.Builder().withId(1).withCustomer(CUSTOMER).withDestination(DESTINATION)
                .withLineItems(new HashSet<>()).withModifiedBy(CUSTOMER).withStatus(OrderStatus.APPROVED).build();
        order = orderService.submitOrder(firstVersion);
    }

    @Test
    public void canSubmitNewVersion() {
        assertEquals("Order is saved", order, orderService.getOrder(order.getId()));
        assertEquals("Order contains submitted version", firstVersion, orderService.getOrder(order.getId()).getVersions().get(ORDER_SUBMITTED_DATE_TIME));
    }

    @Test
    public void canRejectOrder() {
        String note = "Reject note.";
        orderService.rejectOrder(order, note, MODIFIED_BY);

        assertEquals("Note is saved.", note, orderService.getOrder(order.getId()).getNote().get());
        assertEquals("Note is saved.", note, orderService.getOrder(order.getId()).getNote().get());
        assertEquals("Status set to rejected.", OrderStatus.REJECTED, orderService.getOrder(order.getId()).getStatus());
        assertEquals("Modified employee updated", MODIFIED_BY, order.getModifiedBy());
    }

    @Ignore
    @Test
    public void oldVersionsAreSavedWhenOrderUpdated() {
        orderService.rejectOrder(order, null, null);
//        OrderVersion updatedVersion = firstVersion.setId(2).setStatus(OrderStatus.REJECTED)
        assertEquals("First version saved in history.", firstVersion, order.getVersions().get(ORDER_SUBMITTED_DATE_TIME));
    }
}
