package gov.nysenate.ess.supply.statistics.location;

import gov.nysenate.ess.core.client.view.LocationView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
public class LocationStatisticView implements ViewObject {

    protected LocationView location;
    protected Map<String, Integer> itemQuantities;

    public LocationStatisticView() {}

    public LocationStatisticView(LocationStatistic locationStatistic) {
        location = new LocationView(locationStatistic.getLocation());
        itemQuantities = locationStatistic.calculate();
    }

    @XmlElement
    public LocationView getLocation() {
        return location;
    }

    @XmlElement
    public Map<String, Integer> getItemQuantities() {
        return itemQuantities;
    }

    @Override
    public String getViewType() {
        return "location-statistic";
    }
}
