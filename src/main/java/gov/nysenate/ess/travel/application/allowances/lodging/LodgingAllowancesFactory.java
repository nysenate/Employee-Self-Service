package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.travel.application.destination.Destination;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LodgingAllowancesFactory {

    private GsaAllowanceService gsaService;

    @Autowired
    public LodgingAllowancesFactory(GsaAllowanceService gsaService) {
        this.gsaService = gsaService;
    }

    public LodgingAllowances createLodgingAllowances(Destinations destinations) throws IOException {
        List<LodgingAllowance> lodgingAllowances = new ArrayList<>();

        for (Destination dest : destinations.getDestinations()) {
            for (LocalDate night: dest.nights()) {
                Dollars lodgingAllowance = gsaService.fetchLodgingRate(night, dest.getAddress());
                lodgingAllowances.add(new LodgingAllowance(dest.getAddress(), night, lodgingAllowance, true));
            }
        }

        return new LodgingAllowances(lodgingAllowances);
    }
}
