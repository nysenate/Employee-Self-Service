package gov.nysenate.ess.supply.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class ItemStatisticView implements ViewObject {

    protected Map<String, Integer> itemStatistics;

    public ItemStatisticView(ItemStatistic itemStatistic) {
        this.itemStatistics = itemStatistic.calculate();
    }

    @XmlElement
    public Map<String, Integer> getItemStatistics() {
        return itemStatistics;
    }

    @Override
    public String getViewType() {
        return "item-statistics";
    }
}
