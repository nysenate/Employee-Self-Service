package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(GsaAllowanceService.class);

    private GsaBatchResponseDao gsaBatchResponseDao;
    private GsaApi gsaApi;

    @Autowired
    public GsaAllowanceService(GsaBatchResponseDao gsaBatchResponseDao, GsaApi gsaApi) {
        this.gsaBatchResponseDao = gsaBatchResponseDao;
        this.gsaApi = gsaApi;
    }

    /**
     * Returns the MealTier for the given date and address.
     * @throws IOException
     */
    public Dollars fetchMealRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = fetchGsaResponse(date, address);
        return new Dollars(res.getMealTier());
    }

    /**
     * Returns the lodging rate for the given date and address.
     * @throws IOException
     */
    public Dollars fetchLodgingRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = fetchGsaResponse(date, address);
        return new Dollars(res.getLodging(date)); // TODO use dollars in GsaResponse
    }

    private GsaResponse fetchGsaResponse(LocalDate date, Address address) throws IOException {
        GsaResponse res = gsaBatchResponseDao.getGsaData(new GsaResponseId( date.getYear(), address.getZip5() ));
        if (res == null) {
            res = gsaApi.queryGsa(date, address.getZip5());
            if (res != null) {
                gsaBatchResponseDao.handleNewData(res);
            }
        }
        return res;
    }
}
