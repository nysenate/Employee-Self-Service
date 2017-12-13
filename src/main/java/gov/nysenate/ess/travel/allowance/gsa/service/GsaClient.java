package gov.nysenate.ess.travel.allowance.gsa.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.travel.allowance.gsa.dao.MealIncidentalRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealIncidentalRate;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

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
            URL url = new URL("https://inventory.data.gov/api/action/datastore_search?" +
                    "resource_id=8ea44bc4-22ba-4386-b84c-1494ab28964b" +
                    "&filters=%7B%22FiscalYear%22:%22" + fiscalYear + "%22,%22Zip%22:%22" + zipcode + "%22%7D");
            uri = url.toURI();
            System.out.println(uri.toString());
        } catch (MalformedURLException | URISyntaxException e) {
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
        String monthString = month.getDisplayName(TextStyle.SHORT, Locale.US);
        lodging = records.get(monthString).getAsInt();
    }

    public int getBreakfastCost() {
        return mealIncidentalRate.getBreakfastCost();
    }

    public int getDinnerCost() {
        return mealIncidentalRate.getDinnerCost();
    }
}
