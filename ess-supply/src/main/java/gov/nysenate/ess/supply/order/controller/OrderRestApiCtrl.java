package gov.nysenate.ess.supply.order.controller;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderStatus;
import gov.nysenate.ess.supply.order.service.OrderService;
import gov.nysenate.ess.supply.order.view.NewOrderView;
import gov.nysenate.ess.supply.order.view.OrderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supply/orders")
public class OrderRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(OrderRestApiCtrl.class);

    @Autowired
    private OrderService orderService;

    /**
     * Get orders by status and date range.
     * @param status Array of OrderStatus's. Orders with these statuses will be included in response.
     *               Valid types are: PENDING, PROCESSING, COMPLETED, REJECTED.
     *               Defaults to all statuses.
     * @param from Start of date range
     * @param to End of date range
     */
    @RequestMapping("")
    public BaseResponse getOrders(@RequestParam(required = false) String[] status,
                                  @RequestParam(required = false) String from,
                                  @RequestParam(required = false) String to) {
        EnumSet<OrderStatus> statusEnumSet = parseStatuses(status);
        Range<LocalDate> dateRange = parseDateRange(from, to);
        return ListViewResponse.of(orderService.getOrders(statusEnumSet, dateRange).stream()
                                               .map(OrderView::new).collect(Collectors.toList()));
    }

    /**
     * Gets an order by it's id.
     */
    @RequestMapping("/{id:\\d}")
    public BaseResponse getOrderById(@PathVariable int id) {
        Order order = orderService.getOrderById(id);
        return new ViewObjectResponse<>(new OrderView(order));
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse submitOrder(@RequestBody NewOrderView newOrder) {
        int customerId = newOrder.getCustomerId();
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView: newOrder.getItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Order order = orderService.submitOrder(customerId, lineItems);
        return new ViewObjectResponse<>(new OrderView(order));
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse processOrder(@RequestBody OrderView orderView) {
        Order order = orderService.processOrder(orderView.getId(), orderView.getIssuingEmployee().getEmployeeId());
        return new ViewObjectResponse<>(new OrderView(order));
    }

    @RequestMapping(value = "/complete", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse completeOrder(@RequestBody OrderView orderView) {
        Order order = orderService.completeOrder(orderView.getId());
        return new ViewObjectResponse<>(new OrderView(order));
    }

    // TODO: save employee who rejected order
    @RequestMapping(value = "/reject", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse rejectOrder(@RequestBody OrderView orderView) {
        Order order = orderService.rejectOrder(orderView.getId());
        return new ViewObjectResponse<>(new OrderView(order));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse updateOrderItems(@RequestBody OrderView orderView) {
        Order order = orderView.toOrder();
        orderService.saveOrder(order);
        return new ViewObjectResponse<>(new OrderView(order));
    }

    @RequestMapping(value = "/complete/undo", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse undoCompletion(@RequestBody OrderView orderView) {
        Order order = orderView.toOrder();
        orderService.undoCompletion(order.getId());
        return new ViewObjectResponse<>(new OrderView(order));
    }

    private EnumSet<OrderStatus> parseStatuses(String[] statuses) {
        if (statuses != null && statuses.length > 0) {
            return EnumSet.copyOf(Arrays.asList(statuses).stream()
                                        .map(status -> getEnumParameter("status", status, OrderStatus.class))
                                        .collect(Collectors.toSet()));
        }
        else {
            return EnumSet.allOf(OrderStatus.class);
        }
    }

    private Range<LocalDate> parseDateRange(String from, String to) {
        LocalDate toDate = to == null ? LocalDate.now() : parseISODate(to, "to");
        LocalDate fromDate = from == null ? LocalDate.of(toDate.getYear(), 1, 1) : parseISODate(from, "from");
        return getClosedRange(fromDate, toDate, "from", "to");
    }
}
