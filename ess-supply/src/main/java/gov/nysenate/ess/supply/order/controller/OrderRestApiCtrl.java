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
    public void submitOrder(@RequestBody NewOrderView newOrder) {
        int customerId = newOrder.getCustomerId();
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView: newOrder.getItems()) {
            lineItems.add(lineItemView.toLineItem());
        }
        orderService.submitOrder(customerId, lineItems);
    }


}
