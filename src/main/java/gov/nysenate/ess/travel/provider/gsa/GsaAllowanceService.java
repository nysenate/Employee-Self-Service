package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.provider.gsa.client.GsaClient;
import gov.nysenate.ess.travel.provider.gsa.client.GsaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(GsaAllowanceService.class);

    private GsaClient client;

    @Autowired
    public GsaAllowanceService(GsaClient client) {
        this.client = client;
    }

    /**
     * Returns the MealTier for the given date and address.
     * @throws IOException
     */
    public Dollars fetchMealRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = client.queryGsa(date, address.getZip5());
        return new Dollars(res.getMealTier());
    }

    /**
     * Returns the lodging rate for the given date and address.
     * @throws IOException
     */
    public Dollars fetchLodgingRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = client.queryGsa(date, address.getZip5());
        return new Dollars(res.getLodging(date)); // TODO use dollars in GsaResponse
    }
}
