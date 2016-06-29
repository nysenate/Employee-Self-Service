package gov.nysenate.ess.supply.requisition.view;

import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.supply.requisition.Requisition;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

/**
 * Adds requisition history to a {@link RequisitionView}.
 */
@XmlRootElement
public class DetailedRequisitionView extends RequisitionView {

    protected MapView<LocalDateTime, RequisitionVersionView> history;

    public DetailedRequisitionView() {}

    public DetailedRequisitionView(Requisition requisition) {
        super(requisition);
        Map<LocalDateTime, RequisitionVersionView> historyMap = new TreeMap<>();
        for (Map.Entry<LocalDateTime, RequisitionVersion> entry: requisition.getHistory().entrySet()) {
            historyMap.put(entry.getKey(), new RequisitionVersionView(entry.getValue()));
        }
        this.history = MapView.of(historyMap);
    }

    @XmlElement
    public MapView<LocalDateTime, RequisitionVersionView> getHistory() {
        return history;
    }

    public void setHistory(MapView<LocalDateTime, RequisitionVersionView> history) {
        this.history = history;
    }
}
