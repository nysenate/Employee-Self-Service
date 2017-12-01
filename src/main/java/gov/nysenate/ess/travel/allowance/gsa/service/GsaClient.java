package gov.nysenate.ess.travel.allowance.gsa.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.travel.allowance.gsa.dao.MealIncidentalRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealIncidentalRate;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.Month;

@Component
public class GsaClient {

    @Autowired MealIncidentalRatesDao sqlMealIncidentalRatesDao;
    @Value("${travel.gsa.link}") private String gsaLink;

    private static final ResponseHandler<String> responseHandler = response -> {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    private JsonObject records;
    private MealIncidentalRate mealIncidentalRate;
    private int lodging;

    public void scrapeGsa(int fiscalYear, String zipcode){
        CloseableHttpClient httpClient = HttpClients.createDefault();

        URI uri = null;

        try {
            uri = new URIBuilder(gsaLink)
                    .addParameter("filters", "{\"FiscalYear\":\"" + fiscalYear + "\",\"Zip\":\"" + zipcode + "\"}").build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpGet httpget = new HttpGet(uri);

        LocalDateTime NOW = LocalDateTime.now();
        if (fiscalYear < NOW.getYear()) {
            records = null;
            throw new IllegalArgumentException();
        }
        else {
            String responseBody = null;
            try {
                responseBody = httpClient.execute(httpget, responseHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JsonParser jsonParser = new JsonParser();
            records = jsonParser.parse(responseBody).getAsJsonObject();
            records = records.get("result").getAsJsonObject().get("records").getAsJsonArray().get(0).getAsJsonObject();

            int meals = records.get("Meals").getAsInt();

            MealIncidentalRate[] dbRates = sqlMealIncidentalRatesDao.getMealIncidentalRates();
            for (MealIncidentalRate dbRate : dbRates) {
                if(meals == dbRate.getTotalCost()){
                    mealIncidentalRate = dbRate;
                    break;
                }
            }
        }

        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLodging() {
        return lodging;
    }

    public void setLodging(Month month) {
        String monthString = month.toString();
        monthString = monthString.substring(0, 3);
        monthString = monthString.substring(0, 1).toUpperCase() + monthString.substring(1).toLowerCase();
        lodging = records.get(monthString).getAsInt();
    }

    public int getBreakfastCost() {
        return mealIncidentalRate.getBreakfastCost();
    }

    public int getDinnerCost() {
        return mealIncidentalRate.getDinnerCost();
    }

    public int getIncidentalCost() {
        return mealIncidentalRate.getIncidentalCost();
    }
}
