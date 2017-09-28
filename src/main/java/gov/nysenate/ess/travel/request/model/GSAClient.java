package gov.nysenate.ess.travel.request.model;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GSAClient {

    private static final ResponseHandler<String> responseHandler = response -> {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        }
        else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    private int fiscalYear;
    private int zipcode;
    private JsonObject records;

    public GSAClient(int fiscalYear, int zipcode) {
        this.fiscalYear = fiscalYear;
        this.zipcode = zipcode;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("https://inventory.data.gov/api/action/datastore_search?" +
                "resource_id=8ea44bc4-22ba-4386-b84c-1494ab28964b&filters=%7B%22" +
                "FiscalYear%22:%22" + fiscalYear + "%22,%22" +
                "Zip%22:%22" + zipcode + "%22%7D");

        String responseBody = null;
        try {
            responseBody = httpClient.execute(httpget, responseHandler);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        records = jsonParser.parse(responseBody).getAsJsonObject();
        records = records.get("result").getAsJsonObject().get("records").getAsJsonArray().get(0).getAsJsonObject();

        try {
            httpClient.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getFiscalYear() {
        return fiscalYear;
    }

    public int getZipcode() {
        return zipcode;
    }

    public JsonObject getRecords() {
        return records;
    }
}
