package gov.nysenate.ess.supply.order.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.order.Order;

public class OrderView implements ViewObject {

    protected int id;
    protected OrderHistoryView history;

    public OrderView() {}

    public OrderView(Order order) {
        this.id = order.getId();
        this.history = new OrderHistoryView(order.getHistory());
    }

    public Order toOrder() {
        return Order.of(this.id, history.toOrderHistory());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderHistoryView getHistory() {
        return history;
    }

    public void setHistory(OrderHistoryView history) {
        this.history = history;
    }

    @Override
    public String getViewType() {
        return "order-view";
    }
}
