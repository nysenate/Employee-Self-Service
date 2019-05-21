package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

@Component
public class GsaResponseParser {

    private ObjectMapper mapper;

    @Autowired
    public GsaResponseParser(ObjectMapper jsonObjectMapper) {
        this.mapper = jsonObjectMapper;
    }

    /**
     * Detects if the "records" object in the response is empty.
     * This occurs when querying for a year that does not exist yet.
     * @param json
     * @return
     */
    public boolean isResponseEmpty(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode records = root.path("result").path("records");
        return records.size() == 0;
    }

    /**
     * Parses the json text received from the GSA API into a {@link GsaResponse}.
     * This method only handles successful responses, errors should be checked for before use.
     * @param json A json string containing the raw text response from a GSA Api call.
     * @return
     * @throws IOException
     */
    public GsaResponse parseGsaResponse(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode records = root.path("result").path("records");
        JsonNode record = findRecord(records);

        int fiscalYear = record.get("FiscalYear").asInt();
        String zip = record.get("Zip").asText();
        Map<Month, BigDecimal> lodgingRates = parseLodgingRates(record);
        String mealTier = record.get("Meals").asText();

        return new GsaResponse(new GsaResponseId(fiscalYear, zip), lodgingRates, mealTier);
    }

    // Finds the correct record to use. Use the County provided record if one is given.
    private JsonNode findRecord(JsonNode records) {
        JsonNode record = null;
        for (JsonNode r : records) {
            // Use the county record if given one.
            if (!r.get("County").asText().equals("")) {
                record = r;
            }
        }
        // If no county record, use the first one.
        if (record == null) {
            record = records.get(0);
        }
        return record;
    }

    public Map<Month, BigDecimal> parseLodgingRates(JsonNode record) {
        Map<Month, BigDecimal> lodgingRates = new HashMap<>();
        lodgingRates.put(Month.JANUARY, new BigDecimal(record.get("Jan").asText()));
        lodgingRates.put(Month.FEBRUARY, new BigDecimal(record.get("Feb").asText()));
        lodgingRates.put(Month.MARCH, new BigDecimal(record.get("Mar").asText()));
        lodgingRates.put(Month.APRIL, new BigDecimal(record.get("Apr").asText()));
        lodgingRates.put(Month.MAY, new BigDecimal(record.get("May").asText()));
        lodgingRates.put(Month.JUNE, new BigDecimal(record.get("Jun").asText()));
        lodgingRates.put(Month.JULY, new BigDecimal(record.get("Jul").asText()));
        lodgingRates.put(Month.AUGUST, new BigDecimal(record.get("Aug").asText()));
        lodgingRates.put(Month.SEPTEMBER, new BigDecimal(record.get("Sep").asText()));
        lodgingRates.put(Month.OCTOBER, new BigDecimal(record.get("Oct").asText()));
        lodgingRates.put(Month.NOVEMBER, new BigDecimal(record.get("Nov").asText()));
        lodgingRates.put(Month.DECEMBER, new BigDecimal(record.get("Dec").asText()));
        return lodgingRates;
    }
}
