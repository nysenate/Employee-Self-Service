package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.utils.Dollars;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(GsaAllowanceService.class);

    private GsaApi gsaApi;

    @Autowired
    public GsaAllowanceService(GsaApi gsaApi) {
        this.gsaApi = gsaApi;
    }

    /**
     * Fetches and returns the GSA Meal tier for the given date and zip5.
     * If no meal tier exists for the given date and zip5, returns Dollars.ZERO.
     *
     * @throws ProviderException if there is an issue connecting to the GSA API.
     */
    @NotNull
    public Dollars fetchMealRate(LocalDate date, String zip5) throws ProviderException {
        GsaResponse res = gsaApi.queryGsaApi(date, zip5);
        return new Dollars(res.getMealTier());
    }

    /**
     * Returns the lodging rate for the given date and zip5.
     * Returns Dollars.ZERO if there is no lodging rate or if the zip5 is outside of CONUS.
     *
     * @throws ProviderException is there is an issue connecting to the GSA API.
     */
    @NotNull
    public Dollars fetchLodgingRate(LocalDate date, String zip5) throws ProviderException {
        GsaResponse res = gsaApi.queryGsaApi(date, zip5);
        return new Dollars(res.getLodging(date));
    }

    private boolean addressIsOutsideUS(TravelAddress address) {
        return !(address.getCountry().equalsIgnoreCase("United States") || address.getCountry().equalsIgnoreCase("US"));
    }
}
