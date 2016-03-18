package gov.nysenate.ess.supply.order.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.order.OrderHistory;
import gov.nysenate.ess.supply.order.OrderVersion;

import java.time.LocalDateTime;
import java.util.SortedMap;
import java.util.TreeMap;

public class OrderHistoryView implements ViewObject {

    protected SortedMap<LocalDateTime, OrderVersionView> history;

    public OrderHistoryView() {}

    public OrderHistoryView(OrderHistory history) {
        this.history = new TreeMap<>();
        history.getHistory().forEach((d, v) -> this.history.put(d, new OrderVersionView(v)));
    }

    public OrderHistory toOrderHistory() {
        SortedMap<LocalDateTime, OrderVersion> historyMap = new TreeMap<>();
        this.history.forEach((d, v) -> historyMap.put(d, v.toOrderVersion()));
        return OrderHistory.of(historyMap);
    }

    public SortedMap<LocalDateTime, OrderVersionView> getHistory() {
        return history;
    }

    public void setHistory(SortedMap<LocalDateTime, OrderVersionView> history) {
        this.history = history;
    }

    @Override
    public String getViewType() {
        return "order-history-view";
    }
}
