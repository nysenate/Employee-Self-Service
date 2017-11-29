package gov.nysenate.ess.travel.allowance.gsa.service;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.travel.allowance.gsa.dao.SqlMealIncidentalRatesDao;
import gov.nysenate.ess.travel.allowance.gsa.model.MealIncidentalRate;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

public class GsaClient {

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

    public GsaClient(int fiscalYear, String zipcode) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://inventory.data.gov/api/action/datastore_search?" +
                "resource_id=8ea44bc4-22ba-4386-b84c-1494ab28964b&filters=%7B%22" +
                "FiscalYear%22:%22" + fiscalYear + "%22,%22" +
                "Zip%22:%22" + zipcode + "%22%7D");

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

            SqlMealIncidentalRatesDao sqlMealIncidentalRatesDao = new SqlMealIncidentalRatesDao();
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
