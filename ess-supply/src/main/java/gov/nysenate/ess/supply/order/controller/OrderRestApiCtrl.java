package gov.nysenate.ess.supply.order.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.service.OrderService;
import gov.nysenate.ess.supply.order.view.NewOrderView;
import gov.nysenate.ess.supply.order.view.OrderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/supplyOrders")
public class OrderRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(OrderRestApiCtrl.class);

    @Autowired
    private OrderService orderService;

    @RequestMapping("")
    public BaseResponse getAllOrders() {
        List<OrderView> orderViews = new ArrayList<>();
        for (Order order : orderService.getOrders()) {
            orderViews.add(new OrderView(order));
        }
        return ListViewResponse.of(orderViews);
    }

    @RequestMapping(value = "/submitOrder", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse submitOrder(@RequestBody NewOrderView newOrder) {
        int customerId = newOrder.getCustomerId();
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView: newOrder.getItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        Order order = orderService.submitOrder(customerId, lineItems);
        return new ViewObjectResponse<>(new OrderView(order));
    }

    // TODO sort by submitted date asc/desc?
    @RequestMapping("/getPending")
    public BaseResponse getPendingOrders() {
        List<Order> pendingOrders = orderService.getPendingOrders();
        return ListViewResponse.of(pendingOrders.stream().map(OrderView::new).collect(Collectors.toList()));
    }

    @RequestMapping("/getProcessing")
    public BaseResponse getProcessingOrders() {
        List<Order> processingOrders = orderService.getProcessingOrders();
        return ListViewResponse.of(processingOrders.stream().map(OrderView::new).collect(Collectors.toList()));
    }

    /** Return list of orders completed today. */
    @RequestMapping("/getCompleted")
    public BaseResponse getCompletedOrders() {
        List<Order> completedOrders = orderService.getCompletedOrders();
        return ListViewResponse.of(completedOrders.stream().map(OrderView::new).collect(Collectors.toList()));
    }

    @RequestMapping("/getRejected")
    public BaseResponse getRejectedOrders() {
        // TODO:
//        List<Order> completedOrders = orderService.get();
//        return ListViewResponse.of(completedOrders.stream().map(OrderView::new).collect(Collectors.toList()));
        return null;
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

    // TODO: add rejected by user.

    @RequestMapping(value = "/reject", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse rejectOrder(@RequestParam OrderView orderView) {
        Order order = orderService.rejectOrder(orderView.getId());
        return new ViewObjectResponse<>(new OrderView(order));
    }

    // edit order

}
