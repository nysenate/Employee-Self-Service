package gov.nysenate.ess.supply.order.controller;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.LimitOffset;
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
import org.springframework.web.context.request.WebRequest;

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
     * Get orders with the ability to filter by location code, location type, issuing employee id, order status, and date range.
     * Request Parameters: locCode - Location code.
     *                     locType - Location type.
     *                     issuerEmpId - Issuing Employee's id.
     *                     status - OrderStatus's that should be returned.
     *                              Valid types are: PENDING, PROCESSING, COMPLETED, REJECTED.
     *                     from - Start of date range.
     *                     to - End of date range
     *
     * Defaults to any locCode, any locType, any issuerEmpId, YTD range, all order statuses.
     * Dates refer to Order.orderDateTime. TODO: need to filter by other dates?
     */
    @RequestMapping("")
    public BaseResponse getOrders(@RequestParam(required = false) String locCode,
                                  @RequestParam(required = false) String locType,
                                  @RequestParam(required = false) String issuerEmpId,
                                  @RequestParam(required = false) String[] status,
                                  @RequestParam(required = false) String from,
                                  @RequestParam(required = false) String to,
                                  WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 25);
        EnumSet<OrderStatus> statusEnumSet = parseStatuses(status);
        Range<LocalDate> dateRange = parseDateRange(from, to);
        String locCodeTerm = (locCode != null && locCode.length() > 0) ? locCode.toUpperCase() : "all";
        String locTypeTerm = (locType != null && locType.length() > 0) ? locType.toUpperCase() : "all";
        String issuerTerm = (issuerEmpId != null && issuerEmpId.length() > 0) ? issuerEmpId : "all";
        return ListViewResponse.of(orderService.getOrders(locCodeTerm, locTypeTerm, issuerTerm, statusEnumSet, dateRange, limOff)
                                               .stream().map(OrderView::new).collect(Collectors.toList()));
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
        for (LineItemView lineItemView: newOrder.getLineItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Order order = orderService.submitOrder(lineItems, customerId);
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
    public BaseResponse updateOrderItems(@RequestBody int orderId, @RequestBody LineItemView[] lineItemViews) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView : lineItemViews) {
            lineItems.add(lineItemView.toLineItem());
        }
        Order order = orderService.updateOrderLineItems(orderId, lineItems);
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
