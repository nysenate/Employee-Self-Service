package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.model.util.UnsuccessfulHttpReqException;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.HttpUtils;
import gov.nysenate.ess.travel.provider.ProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Responsible for getting GsaResponse objects from the official GSA Api.
 * GSA API Docs: <a href="https://open.gsa.gov/api/perdiem/">...</a>
 */
@Service
public class GsaApi {
    private final String baseUrl;
    private final String ratesPathTemplate;
    private final GsaResponseParser gsaResponseParser;

    @Autowired
    public GsaApi(@Value("${travel.gsa.api.url_base}") String baseUrl, @Value("${travel.gsa.api.key}") String apiKey,
                  GsaResponseParser gsaResponseParser) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.ratesPathTemplate = "zip/%s/year/%s?api_key=" + apiKey;
        this.gsaResponseParser = gsaResponseParser;
    }

    /**
     * Returns data from the GSA API in a {@link GsaResponse} object for the given date and zip.
     *
     * @param date The date to get GSA rates for.
     * @param zip  The zipcode to get GSA rates for.
     * @return GsaResponse for the given date and zip.
     * @throws ProviderException
     */
    public GsaResponse queryGsaApi(LocalDate date, String zip) throws ProviderException {
        GsaResponseId id = new GsaResponseId(date, zip);
        return doQueryGsaApi(id);
    }

    private GsaResponse doQueryGsaApi(GsaResponseId id) throws ProviderException {
        // Format the URL with the zip code and fiscal year of the request.
        String url = String.format(baseUrl + ratesPathTemplate,
                URLEncoder.encode(id.getZipcode(), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(id.getFiscalYear()), StandardCharsets.UTF_8));
        try {
            String content = HttpUtils.urlToString(url);
            if (dateTooFarInFuture(id, content)) {
                id = new GsaResponseId(id.getFiscalYear() - 1, id.getZipcode());
                return doQueryGsaApi(id);
            } else if (gsaResponseParser.isResponseEmpty(content)) {
                // If no records are found, return an GsaResponse with no lodging rates and a meal tier of $0.
                // This occurs when querying US locations outside of CONUS. i.e. Alaska, Hawaii.
                return new GsaResponse(id, new HashMap<>(), "0");
            } else {
                return gsaResponseParser.parseGsaResponse(content);
            }
        } catch (IOException | UnsuccessfulHttpReqException ex) {
            throw new ProviderException(ex);
        }
    }

    // When querying for travel far in the future, GSA may not yet have rates available.
    private boolean dateTooFarInFuture(GsaResponseId id, String content) throws IOException {
        return gsaResponseParser.isResponseEmpty(content)
                && id.getFiscalYear() > DateUtils.getFederalFiscalYear(LocalDate.now());
    }
}
