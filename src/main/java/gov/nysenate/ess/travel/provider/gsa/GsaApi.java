package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;


/**
 * Responsible for getting GsaResponse objects from the official GSA Api.
 *
 * GSA API Docs: https://www.gsa.gov/technology/government-it-initiatives/digital-strategy/per-diem-apis/api-for-per-diem-rates
 */
@Service
public class GsaApi {

    private static final Logger logger = LoggerFactory.getLogger(GsaApi.class);

    private HttpUtils httpUtils;
    private String gsaUrl;
    private GsaResponseParser gsaResponseParser;

    @Autowired
    public GsaApi(@Value("${travel.gsa.host.url}") String baseUrl, @Value("${travel.gsa.api.url}") String apiUrl,
                  GsaResponseParser gsaResponseParser, HttpUtils httpUtils) {
        this.gsaUrl = baseUrl + apiUrl + "&filters=";
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
        GsaResponseId id = new GsaResponseId(DateUtils.getFederalFiscalYear(date), zip);
        return queryApi(id);
    }

    private GsaResponse queryApi(GsaResponseId id) throws IOException {
        // Example query: {"FiscalYear":"2018","Zip":"12208"}
        String query = "{\"FiscalYear\":\"" + String.valueOf(id.getFiscalYear())
                + "\",\"Zip\":\"" + id.getZipcode() + "\"}";
        String url = gsaUrl + URLEncoder.encode(query, "UTF-8");
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
}
