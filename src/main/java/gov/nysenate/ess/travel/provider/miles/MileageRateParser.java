package gov.nysenate.ess.travel.provider.miles;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;

public class MileageRateParser {

    /**
     * Parses a Map of {@link }'s from the gsa
     * Meal and Incidental expenses webpage html.
     * Webpage available at:
     * https://www.gsa.gov/travel/plan-book/transportation-airfare-rates-pov-rates-etc/privately-owned-vehicle-pov-mileage-reimbursement-rates
     *
     * @param content
     * @return
     */
    public MileageRate parseMileageRate(String content) {
        Document document = Jsoup.parse(content);
        Elements rows = document.select("tbody>tr[class=odd]");
        return getMileageInfoFromTableRows(rows);
    }

    private MileageRate getMileageInfoFromTableRows(Elements rows) {
        Elements tds = rows.get(0).getElementsByTag("td");

        Element middle_td_strong = tds.get(1);
        Element last_td = tds.get(2);

        String mileageRate = format(last_td.text());
        LocalDate finalStartdate;
        LocalDate endDate;

        String startDateRaw = middle_td_strong.getElementsByTag("Strong").text();
        startDateRaw = startDateRaw.replace(",","");
        String[] splitStartDate = startDateRaw.split(" ");

        finalStartdate = LocalDate.of(
                Integer.parseInt(splitStartDate[2]),
                Month.valueOf(splitStartDate[0].toUpperCase()),
                Integer.parseInt(splitStartDate[1])
        );

        endDate = LocalDate.of(3000, Month.DECEMBER, 31);

        return new MileageRate(finalStartdate, endDate, mileageRate);

    }

    /**
     * Remove the dollar sign from the values.
     */
    private String format(String text) {
        return text.replace("$", "");
    }
}
