package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.service.notification.slack.service.DefaultSlackChatService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMie;
import gov.nysenate.ess.travel.provider.gsa.meal.SqlGsaMieDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class GsaAllowanceService {

    private static final Logger logger = LoggerFactory.getLogger(GsaAllowanceService.class);

    private GsaBatchResponseDao gsaBatchResponseDao;
    private GsaApi gsaApi;
    private DefaultSlackChatService slackChatService;
    private SqlGsaMieDao gsaMieDao;

    @Autowired
    public GsaAllowanceService(GsaBatchResponseDao gsaBatchResponseDao, GsaApi gsaApi,
                               DefaultSlackChatService slackChatService, SqlGsaMieDao gsaMieDao) {
        this.gsaBatchResponseDao = gsaBatchResponseDao;
        this.gsaApi = gsaApi;
        this.slackChatService = slackChatService;
        this.gsaMieDao = gsaMieDao;
    }

    public GsaMie fetchGsaMie(LocalDate date, Address address) throws IOException {
        GsaResponse res = fetchGsaResponse(date, address);
        return gsaMieDao.selectGsaMie(res.getId().getFiscalYear(), new Dollars(res.getMealTier()));
    }

    /**
     * Returns the MealTier for the given date and address.
     *
     * @throws IOException
     */
    public Dollars fetchMealRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = fetchGsaResponse(date, address);
        return new Dollars(res.getMealTier());
    }

    /**
     * Returns the lodging rate for the given date and address.
     *
     * @throws IOException
     */
    public Dollars fetchLodgingRate(LocalDate date, Address address) throws IOException {
        GsaResponse res = fetchGsaResponse(date, address);
        return new Dollars(res.getLodging(date)); // TODO use dollars in GsaResponse
    }

    @Scheduled(cron = "${gsa.cron.data:0 0 7 1,7,14,21,28 * *}")
    public void refreshGsaMieData() throws IOException {
        refreshGsaMieData(DateUtils.getFederalFiscalYear(LocalDate.now()));
    }

    public void refreshGsaMieData(int fiscalYear) throws IOException {
        logger.info("Refreshing GSA mie data for fiscal year: " + fiscalYear + " and " + (fiscalYear + 1));
        List<GsaMie> mies = new ArrayList<>();
        // Query rates for the given fiscal year.
        mies.addAll(gsaApi.queryGsaMie(fiscalYear));
        // See if next years rates are available.
        mies.addAll(gsaApi.queryGsaMie(fiscalYear + 1));

        gsaMieDao.saveGsaMies(mies);
    }

    private GsaResponse fetchGsaResponse(LocalDate date, Address address) throws IOException {
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
        return res;
    }
}
