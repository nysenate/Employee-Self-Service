package gov.nysenate.ess.travel.request.allowances.lodging;

import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LodgingPerDiemService {

    private GsaAllowanceService gsaAllowanceService;

    @Autowired
    public LodgingPerDiemService(GsaAllowanceService gsaAllowanceService) {
        this.gsaAllowanceService = gsaAllowanceService;
    }

    public LodgingPerDiem createLodgingPerDiem(LocalDate date, TravelAddress address) {
        Dollars lodgingRate = gsaAllowanceService.fetchLodgingRate(date, address);
        return new LodgingPerDiem(address, date, lodgingRate);
    }
}
