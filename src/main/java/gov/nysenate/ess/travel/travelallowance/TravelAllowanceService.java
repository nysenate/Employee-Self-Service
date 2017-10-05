package gov.nysenate.ess.travel.travelallowance;

import gov.nysenate.ess.travel.application.dao.InMemoryTravelApplicationDao;
import gov.nysenate.ess.travel.application.model.TransportationAllowance;
import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelApplicationStatus;
import org.springframework.stereotype.Service;

public class TravelAllowanceService {

    private TravelApplication travelApp;

    public TravelAllowanceService(TravelApplication travelApp) {
        this.travelApp = travelApp;
    }

    public void updateTravelAllowance() {
        this.travelApp.Builder().setTransportationAllowance(new TransportationAllowance("0", "0"));
    }
}
