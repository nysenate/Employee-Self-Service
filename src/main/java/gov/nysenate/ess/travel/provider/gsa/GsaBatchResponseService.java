package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;

@Service
public class GsaBatchResponseService {

    private static final Logger logger = LoggerFactory.getLogger(GsaBatchResponseService.class);
    private final GsaResponseParser gsaResponseParser;
    private ObjectMapper mapper;
    private GsaBatchResponseDao gsaBatchResponseDao;
    private HttpUtils httpUtils;


    @Value("${travel.gsa.api.url_path}") private String apiUrl;
    @Value("${travel.gsa.api.url_base}")private String hostUrl;
    private String limit = "&limit=";


    @Autowired
    public GsaBatchResponseService(GsaResponseParser gsaResponseParser, ObjectMapper jsonObjectMapper,
                                   GsaBatchResponseDao gsaBatchResponseDao, HttpUtils httpUtils) {
        this.gsaResponseParser = gsaResponseParser;
        this.mapper = jsonObjectMapper;
        this.gsaBatchResponseDao = gsaBatchResponseDao;
        this.httpUtils = httpUtils;
    }

    @Scheduled(cron = "${gsa.cron.data:0 0 0 1 * *}")
    public void scheduledCycleThroughGsaInfo() throws IOException {
        cycleThroughGsaInfo();
    }

    public boolean cycleThroughGsaInfo() throws IOException {
        String nextBatchUrl = "";
        int batchNumber = 1000;
        int offset = 0;
        int total = getTotalNumberOfGsaRecords(
                httpUtils.urlToString(hostUrl + apiUrl + limit + "1"));
        String urlString = hostUrl + apiUrl + limit + batchNumber;

        if (total != 0) {
            String firstResult = httpUtils.urlToString(urlString);
            nextBatchUrl = parseBatchGsaResponse(firstResult);
            offset = offset + batchNumber;

            while (offset < total) {
                logger.info("Processing batch at offset: " + offset + " out of total: " + total);
                urlString = hostUrl + nextBatchUrl;
                String result = httpUtils.urlToString(urlString );
                nextBatchUrl = parseBatchGsaResponse(result);
                offset = offset + batchNumber;
            }

            return true;
        }

        return false;
    }

    private String parseBatchGsaResponse(String json) throws IOException {
        if (!gsaResponseParser.isResponseEmpty(json)) {
            JsonNode root = mapper.readTree(json);
            JsonNode records = root.path("result").path("records");

            for (JsonNode record : records) {
                int fiscalYear = record.get("FiscalYear").asInt();
                String zip = record.get("Zip").asText();
                Map<Month, BigDecimal> lodgingRates = gsaResponseParser.parseLodgingRates(record);
                String mealTier = record.get("Meals").asText();
                String city = record.get("City").asText();
                String county = record.get("County").asText();

                GsaResponse gsaResponse = new GsaResponse(new GsaResponseId(fiscalYear, zip),
                        lodgingRates, mealTier, city, county);

                //send to database
                gsaBatchResponseDao.handleNewData(gsaResponse);
            }
            return root.path("result").get("_links").get("next").asText();
        }
        return "";
    }

    private int getTotalNumberOfGsaRecords(String json) throws IOException {
        if (!gsaResponseParser.isResponseEmpty(json)) {
            JsonNode root = mapper.readTree(json);
            return root.path("result").path("total").asInt();
        }
        return 0;
    }

}
