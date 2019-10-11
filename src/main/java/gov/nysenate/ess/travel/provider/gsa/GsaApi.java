package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.HttpUtils;
import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMealIncidentalExpenses;
import gov.nysenate.ess.travel.provider.gsa.meal.GsaMealIncidentalExpensesView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for getting GsaResponse objects from the official GSA Api.
 *
 * GSA API Docs: https://open.gsa.gov/api/perdiem/
 */
@Service
public class GsaApi {

    private static final Logger logger = LoggerFactory.getLogger(GsaApi.class);

    private HttpUtils httpUtils;
    private String baseUrl;
    private String ratesPathTemplate;
    private String miePathTemplate;
    private GsaResponseParser gsaResponseParser;

    @Autowired
    public GsaApi(@Value("${travel.gsa.api.url_base}") String baseUrl, @Value("${travel.gsa.api.key}") String apiKey,
                  GsaResponseParser gsaResponseParser, HttpUtils httpUtils) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.ratesPathTemplate = "zip/%s/year/%s?api_key=" + apiKey;
        this.miePathTemplate = "conus/mie/%s?api_key=" + apiKey;
        this.gsaResponseParser = gsaResponseParser;
        this.httpUtils = httpUtils;
    }

    /**
     * Returns data from the GSA API in a {@link GsaResponse} object for the given date and zip.
     * @param date The date to get GSA rates for.
     * @param zip The zipcode to get GSA rates for.
     * @return GsaResponse for the given date and zip.
     * @throws IOException
     */
    public GsaResponse queryGsa(LocalDate date, String zip) throws IOException {
        GsaResponseId id = new GsaResponseId(date, zip);
        return queryApi(id);
    }

    private GsaResponse queryApi(GsaResponseId id) throws IOException {
        // Format the URL with the zip code and fiscal year of the request.
        String url = String.format(baseUrl + ratesPathTemplate, id.getZipcode(), String.valueOf(id.getFiscalYear()));
        String content = httpUtils.urlToString(url);
        if (dateTooFarInFuture(id, content)) {
            id = new GsaResponseId(id.getFiscalYear() - 1, id.getZipcode());
            return queryApi(id);
        }
        else {
            return gsaResponseParser.parseGsaResponse(content);
        }
    }

    // When querying for travel far in the future, GSA may not yet have rates available.
    private boolean dateTooFarInFuture(GsaResponseId id, String content) throws IOException {
        return gsaResponseParser.isResponseEmpty(content)
                && id.getFiscalYear() > DateUtils.getFederalFiscalYear(LocalDate.now());
    }

    /**
     * Queries all meal and incidental rates for a given fiscal year.
     * @param fiscalYear
     * @return
     * @throws IOException
     */
    public List<GsaMealIncidentalExpenses> queryGsaMealRates(int fiscalYear) throws IOException {
        String url = String.format(baseUrl + miePathTemplate, String.valueOf(fiscalYear));
        String content = httpUtils.urlToString(url);
        GsaMealIncidentalExpensesView[] res = OutputUtils.jsonToObject(content, GsaMealIncidentalExpensesView[].class);
        return Arrays.stream(res).map(mie -> mie.toGsaMealRate(fiscalYear)).collect(Collectors.toList());
    }
}
