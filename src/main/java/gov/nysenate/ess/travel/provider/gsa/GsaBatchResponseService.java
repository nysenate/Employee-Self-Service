package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Month;
import java.util.Map;

import static org.apache.http.HttpHeaders.USER_AGENT;

@Service
public class GsaBatchResponseService {

    private static final Logger logger = LoggerFactory.getLogger(GsaBatchResponseService.class);
    private final GsaResponseParser gsaResponseParser;
    private ObjectMapper mapper;
    private GsaBatchResponseDao gsaBatchResponseDao;

    private final String hostUrl = "https://inventory.data.gov";
    private String url = "/api/action/datastore_search?resource_id=8ea44bc4-22ba-4386-b84c-1494ab28964b&limit=";
    private String initialNumber = "1";
    private int batchNumber = 1000;
    private int offset = 0;
    private int total;
    private String nextBatchUrl = "";

    @Autowired
    public GsaBatchResponseService(GsaResponseParser gsaResponseParser, ObjectMapper jsonObjectMapper,
                                   GsaBatchResponseDao gsaBatchResponseDao) {
        this.gsaResponseParser = gsaResponseParser;
        this.mapper = jsonObjectMapper;
        this.gsaBatchResponseDao = gsaBatchResponseDao;
    }

    @Scheduled(cron = "* * * 1 * *")
    public void scheduledCycleThroughGsaInfo() throws IOException {
        cycleThroughGsaInfo();
    }


    public boolean cycleThroughGsaInfo() throws IOException {
        this.total = getTotalNumberOfGsaRecords(
                contactGsa(hostUrl + url + initialNumber));

        if (total != 0) {
            String firstResult = contactGsa(hostUrl + url + batchNumber);
            setNextBatchUrl(parseBatchGsaResponse(firstResult));
            offset = offset + batchNumber;

            while (offset < total) {
                logger.info("Processing batch at offset: " + offset + " out of total: " + total);
                String result = contactGsa(hostUrl + nextBatchUrl );
                setNextBatchUrl(parseBatchGsaResponse(result));
                offset = offset + batchNumber;
            }

            return true;
        }

        return false;
    }

    public String contactGsa(String gsaUrl) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(gsaUrl);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        HttpResponse response = client.execute(request);

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    private void setNextBatchUrl(String nextBatchUrl) {
        this.nextBatchUrl = nextBatchUrl;
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
