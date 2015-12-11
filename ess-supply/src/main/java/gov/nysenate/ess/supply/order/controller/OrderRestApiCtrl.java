package gov.nysenate.ess.supply.order.controller;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.view.LineItemView;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.service.OrderService;
import gov.nysenate.ess.supply.order.view.OrderView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // TODO: post should take one param, combine emp id and lineitems into view object
    @RequestMapping(value = "/submitOrder", method = RequestMethod.POST, consumes = "application/json")
    public BaseResponse something(@RequestParam String empId, @RequestParam LineItemView[] lineItemViews) {
        Set<LineItem> lineItems = new HashSet<>();
        for (LineItemView lineItemView: lineItemViews) {
            lineItems.add(lineItemView.toLineItem());
        }
        orderService.submitOrder(Integer.valueOf(empId), lineItems);
        return null;
    }


}
