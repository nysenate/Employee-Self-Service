package gov.nysenate.ess.supply.order.view;

import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.order.Order;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class OrderView implements ViewObject {

    protected int id;
    protected OrderVersionView activeVersion;
    protected MapView<LocalDateTime, OrderVersionView> history;

    public OrderView() {}

    public OrderView(Order order) {
        this.id = order.getId();
        this.activeVersion = new OrderVersionView(order.current());
        Map<LocalDateTime, OrderVersionView> historyMap = new TreeMap<>();
        order.getHistory().getHistory().forEach((d, v) -> historyMap.put(d, new OrderVersionView(v)));
        this.history = MapView.of(historyMap);
    }

    public Order toOrder() {
        SortedMap<LocalDateTime, OrderVersion> historyMap = new TreeMap<>();
        this.history.items.forEach((d, v) -> historyMap.put(d, v.toOrderVersion()));
        return Order.of(this.id, OrderHistory.of(historyMap));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String getViewType() {
        return "order-view";
    }
}
