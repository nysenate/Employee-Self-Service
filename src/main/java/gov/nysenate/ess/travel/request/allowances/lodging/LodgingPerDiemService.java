package gov.nysenate.ess.travel.request.allowances.lodging;

import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class LodgingPerDiemService {

    private GsaAllowanceService gsaAllowanceService;

    @Autowired
    public LodgingPerDiemService(GsaAllowanceService gsaAllowanceService) {
        this.gsaAllowanceService = gsaAllowanceService;
    }

    public LodgingPerDiems createLodgingPerDiems(Route route) {
        Set<LodgingPerDiem> lpdSet = new HashSet<>();
        for (Destination d : route.destinations()) {
            for (LocalDate nightOfStay : d.nights()) {
                lpdSet.add(createLodgingPerDiem(nightOfStay, d.getAddress()));
            }
        }
        return new LodgingPerDiems(lpdSet);
    }

    public LodgingPerDiem createLodgingPerDiem(LocalDate date, TravelAddress address) {
        Dollars lodgingRate = gsaAllowanceService.fetchLodgingRate(date, address.getZip5());
        return new LodgingPerDiem(address, date, lodgingRate);
    }
}
