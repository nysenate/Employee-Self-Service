package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.service.notification.slack.service.DefaultSlackChatService;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.provider.ProviderException;
import gov.nysenate.ess.travel.provider.senate.SqlSenateMieDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GsaAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(GsaAllowanceService.class);

    private GsaBatchResponseDao gsaBatchResponseDao;
    private GsaApi gsaApi;
    private DefaultSlackChatService slackChatService;
    private SqlSenateMieDao gsaMieDao;

    @Autowired
    public GsaAllowanceService(GsaBatchResponseDao gsaBatchResponseDao, GsaApi gsaApi,
                               DefaultSlackChatService slackChatService, SqlSenateMieDao gsaMieDao) {
        this.gsaBatchResponseDao = gsaBatchResponseDao;
        this.gsaApi = gsaApi;
        this.slackChatService = slackChatService;
        this.gsaMieDao = gsaMieDao;
    }

    /**
     * Returns the MealTier for the given date and address.
     *
     * @throws ProviderException
     */
    public Dollars fetchMealRate(LocalDate date, TravelAddress address) throws ProviderException {
        if (addressIsOutsideUS(address)) {
            return Dollars.ZERO;
        }
        GsaResponse res = fetchGsaResponse(date, address);
        return new Dollars(res.getMealTier());
    }

    /**
     * Returns the lodging rate for the given date and address.
     * Returns Dollars.ZERO if there is no lodging rate.
     * @throws ProviderException
     */
    public Dollars fetchLodgingRate(LocalDate date, TravelAddress address) throws ProviderException {
        if (addressIsOutsideUS(address)) {
            return Dollars.ZERO;
        }
        GsaResponse res = fetchGsaResponse(date, address);
        return new Dollars(res.getLodging(date)); // TODO use dollars in GsaResponse
    }

    private GsaResponse fetchGsaResponse(LocalDate date, TravelAddress address) throws ProviderException {
        GsaResponse res = gsaApi.queryGsa(date, address.getZip5());
        // TODO Batch/bulk data will have to be refactored due to new GSA API. For now query the API every time.
//        try {
//            res = gsaBatchResponseDao.getGsaData(gsaResponseId);
//        }
//        catch (DataAccessException e) {
//            res = null;
//            String errorMsg = "Unable to load local GSA data for fiscal year: " + gsaResponseId.getFiscalYear()
//                    + " and zip: " + address.getZip5() + ". Exception was: \n" + ExceptionUtils.getStackTrace(e);
//            logger.warn(errorMsg);
//            slackChatService.sendMessage(errorMsg);
//        }
//        if (res == null) {
//            res = gsaApi.queryGsa(date, address.getZip5())
//            if (res != null) {
//                gsaBatchResponseDao.handleNewData(res);
//            }
//        }
        return gsaApi.queryGsa(date, address.getZip5());
    }

    private boolean addressIsOutsideUS(TravelAddress address) {
        return !(address.getCountry().equalsIgnoreCase("United States") || address.getCountry().equalsIgnoreCase("US"));
    }
}
