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
        JsonNode records = root.path("rates");
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
        JsonNode record = root.path("rates").get(0); // only ever 1 record. (Called "rates" in the response)
        JsonNode rates = record.path("rate"); // List of matching locations/rates. A single zip can have multiple rates.
        JsonNode highestRate = findRecord(rates);

        int fiscalYear = record.get("year").asInt();
        String zip = highestRate.get("zip").asText();
        Map<Month, BigDecimal> lodgingRates = parseLodgingRates(highestRate);
        String mealTier = highestRate.get("meals").asText();

        return new GsaResponse(new GsaResponseId(fiscalYear, zip), lodgingRates, mealTier);
    }

    // Finds the correct record to use.
    // Senate policy is to use the County provided record if one is given.
    private JsonNode findRecord(JsonNode rates) {
        JsonNode record = null;
        for (JsonNode r : rates) {
            // Use the county record if given one.
            if (!r.get("county").asText().equals("")) {
                record = r;
            }
        }
        // If no county record, use the first one.
        if (record == null) {
            record = rates.get(0);
        }
        return record;
    }

    public Map<Month, BigDecimal> parseLodgingRates(JsonNode record) {
        Map<Month, BigDecimal> lodgingRates = new HashMap<>();

        JsonNode lodgingRatesNode = record.path("months").path("month");
        for (JsonNode month : lodgingRatesNode) {
            lodgingRates.put(Month.of(month.get("number").asInt()), new BigDecimal(month.get("value").asText()));
        }
        return lodgingRates;
    }
}
