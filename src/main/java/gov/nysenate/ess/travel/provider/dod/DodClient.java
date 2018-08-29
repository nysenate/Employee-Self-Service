package gov.nysenate.ess.travel.provider.dod;

import com.google.common.collect.Range;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@Component
public class DodClient {
    private final String dodUrl = "http://www.defensetravel.dod.mil/site/perdiemCalc.cfm";

    private Map<String, String> cookies = null;

    private static final Logger logger = LoggerFactory.getLogger(DodClient.class);

    public DodClient() {
    }


    public Document connectToDod() throws IOException {

        Connection.Response response = Jsoup.connect(dodUrl)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .referrer("http://www.google.com")
                .timeout(12000)
                .followRedirects(true)
                .validateTLSCertificates(false)
                .execute();
        cookies = response.cookies();
        return response.parse();
    }

    public Document connectToDodPerDiem(Connection conn) throws IOException {

        return conn
                .referrer(dodUrl)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .followRedirects(true)
                .header("Accept-Encoding", "gzip, deflate, br")
                .ignoreContentType(true)
                .method(Connection.Method.POST)
                .timeout(12000)
                .validateTLSCertificates(false)
                .cookie("CFID", cookies.get("CFID"))
                .cookie("CFTOKEN", cookies.get("CFTOKEN"))
                .cookie("JSESSIONID", cookies.get("JSESSIONID"))
                .execute()
                .parse();
    }

    public void selectFromDodLandingForm(Document doc, String country, LocalDate travelDate) {
        //Handle Country / State Dropdown
        Elements selectCountryOptions = doc.select("select[name=country] > option");
        boolean matched = false;

        for (Element option : selectCountryOptions) {
            if (option.attr("value").equals(country.toUpperCase()) && !matched) {
                matched = true;
                option.attr("selected", "selected");
            } else {
                option.removeAttr("selected");
            }
        }

        //Handle Date dropdown
        String travelYear = String.valueOf(travelDate.getYear());
        String dateValue = "01" + " " + travelDate.getMonth().toString().substring(0,3) + " " + travelYear.substring(travelYear.length() - 2);
        Elements selectDateOptions = doc.select("select[id=DATE] > option" );

        for (Element option: selectDateOptions) {
            if ( option.text().equals(dateValue) ) {
                option.attr("selected", "selected");
            }
            else {
                option.removeAttr("selected");
            }
        }

    }

    public Connection submitDodLandingForm(Document doc) {
        return doc.select("form[name=oconus]").forms().get(0).submit();
    }

    public DodMealTier gatherCityInfo(Document dataDoc, String city, LocalDate travelDate) {

        boolean matched = false;
        Element table = dataDoc.getElementsByTag("table").get(0);
        Elements rows = table.getElementsByTag("tr");

        ArrayList<DodMealTier> dodMealTiers = getListOfDodMealTiers(rows);
        //Other is always the last row in the table. without any direct matches, we want this row returned
        DodMealTier matchedMealTier = dodMealTiers.get(dodMealTiers.size() - 1);

        for (DodMealTier mealTier : dodMealTiers) {
            //see if we find a city match at all
            if (city.equalsIgnoreCase(mealTier.getLocation()) && !matched) {
                matched = true;
                matchedMealTier = mealTier;
            }
            //see if there is a second match and if so determine which match needs to be returned
            else if (city.equalsIgnoreCase(mealTier.getLocation()) && matched) {
                if (mealTier.getSeason().contains(travelDate)) {
                    matchedMealTier = mealTier;
                }
            }
        }

        return matchedMealTier;
    }

    private ArrayList<DodMealTier> getListOfDodMealTiers(Elements rows) {

        ArrayList<DodMealTier> dodMealTiers = new ArrayList<>();

        for (Element row : rows) { //cycle through all rows

            Elements data = row.getElementsByTag("td"); //get table data tags

            if (data.size() == 0) { //go to next row if there are no td tags
                continue;
            } else {
                DodMealTier generatedMealTier = getMealTierFromRow(data);
                if (generatedMealTier!= null) {
                    dodMealTiers.add(generatedMealTier);
                }

            }
        }

        return dodMealTiers;
    }

    private DodMealTier getMealTierFromRow(Elements data) {

        if (data.size() != 10) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            int currentYear = LocalDate.now().getYear();
            int nextYear = currentYear + 1;

            String location = data.get(0).getElementsByTag("b").text().replaceAll("\\[","").replaceAll("]","");

            String seasonsString = data.get(1).getElementsByTag("font").text();
            String[] seasonsArray = seasonsString.split("-");
            LocalDate seasonStart = LocalDate.parse(seasonsArray[0] + "/" + currentYear, formatter);
            LocalDate seasonEnd = LocalDate.parse(seasonsArray[1] + "/" + currentYear, formatter);
            if(seasonEnd.isBefore(seasonStart)) {
                seasonEnd = seasonEnd.withYear(nextYear);
            }

            Range<LocalDate> season = Range.open(seasonStart, seasonEnd);

            String lodging = data.get(2).getElementsByTag("font").text();
            String total = data.get(3).getElementsByTag("font").text();
            String indicental = data.get(5).getElementsByTag("font").text();
            LocalDate effectiveDate = LocalDate.parse(data.get(9).getElementsByTag("font").text(), formatter);

            return new DodMealTier(location, season, lodging, total, indicental, effectiveDate);
        }
        catch (Exception e) {

            return null;
        }

    }

}
