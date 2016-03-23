package gov.nysenate.ess.supply.order.controller;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.*;
import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.item.view.UpdateLineItemView;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderService;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.OrderVersion;
import gov.nysenate.ess.supply.order.view.OrderView;
import gov.nysenate.ess.supply.order.view.SubmitOrderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/orders")
public class OrderRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(OrderRestApiCtrl.class);

    private OrderService orderService;
    private EmployeeInfoService employeeService;
    private LocationService locationService;

    @Autowired
    public OrderRestApiCtrl(OrderService orderService, EmployeeInfoService employeeService, LocationService locationService) {
        this.orderService = orderService;
        this.employeeService = employeeService;
        this.locationService = locationService;
    }

    @RequestMapping("/{id}")
    public BaseResponse getOrderById(@PathVariable int id) {
        Order order = orderService.getOrder(id);
        return new ViewObjectResponse<>(new OrderView(order));
    }

    @RequestMapping("")
    public BaseResponse searchOrders(@RequestParam(defaultValue = "all", required = false) String location,
                                     @RequestParam(defaultValue = "all", required = false) String customerId,
                                     @RequestParam(required = false) String[] status,
                                     @RequestParam(required = false) String from,
                                     @RequestParam(required = false) String to,
                                     WebRequest webRequest) {
        // Set default values if missing
        LocalDateTime fromDateTime = from == null ? LocalDateTime.now().minusMonths(1) : parseISODateTime(from, "from");
        LocalDateTime toDateTime = to == null ? LocalDateTime.now() : parseISODateTime(to, "to");
        EnumSet<OrderStatus> statuses = status == null ? EnumSet.allOf(OrderStatus.class) : getEnumSetFromStringArray(status);

        // TODO: validate data before trying to get orders?
        LimitOffset limoff = getLimitOffset(webRequest, 25);
        Range<LocalDateTime> dateRange = getClosedRange(fromDateTime, toDateTime, "from", "to");
        PaginatedList<Order> results = orderService.getOrders(location, customerId, statuses, dateRange, limoff);
        List<OrderView> orderViews = results.getResults().stream().map(OrderView::new).collect(Collectors.toList());
        return ListViewResponse.of(orderViews, results.getTotal(), results.getLimOff());
    }

    private EnumSet<OrderStatus> getEnumSetFromStringArray(String[] status) {
        List<OrderStatus> statusList = new ArrayList<>();
        for (String s : status) {
            statusList.add(OrderStatus.valueOf(s));
        }
        return EnumSet.copyOf(statusList);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void submitOrder(@RequestBody SubmitOrderView submitOrderView) {
        // TODO: verify these views contain valid data by calling services
        // TODO: extract OrderVersion creation code into factory
        // TODO: submitOrderView should contain the destination Location.
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView : submitOrderView.getLineItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Employee customer = employeeService.getEmployee(submitOrderView.getCustomerId());
        Location loc = customer.getWorkLocation();
        OrderVersion version = new OrderVersion.Builder().withCustomer(customer)
                                                         .withDestination(loc).withLineItems(lineItems)
                                                         .withStatus(OrderStatus.APPROVED).withModifiedBy(customer).build();
        orderService.submitOrder(version);
    }

    @RequestMapping(value = "{id}/reject", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void rejectOrder(@PathVariable int id, @RequestBody(required = false) String note) {
        Order order = orderService.getOrder(id);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        orderService.rejectOrder(order, note, modifiedBy);
    }

    @RequestMapping(value = "{id}/line_items/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateLineItems(@PathVariable int id, @RequestBody UpdateLineItemView updateLineItemView) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView view: updateLineItemView.getLineItems()) {
            lineItems.add(view.toLineItem());
        }
        Order order = orderService.getOrder(id);
        Employee modifiedBy = employeeService.getEmployee(getSubjectEmployeeId());
        orderService.updateLineItems(order, lineItems, updateLineItemView.getNote(), modifiedBy);
    }

    private int getSubjectEmployeeId() {
        SenatePerson person = (SenatePerson) getSubject().getPrincipals().getPrimaryPrincipal();
        return person.getEmployeeId();
    }
}
